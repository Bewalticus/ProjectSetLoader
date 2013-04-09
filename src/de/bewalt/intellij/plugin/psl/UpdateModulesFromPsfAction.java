/*
 * Copyright 2013 by Helge Walter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.bewalt.intellij.plugin.psl;

import com.intellij.cvsSupport2.CvsVcs2;
import com.intellij.cvsSupport2.actions.update.UpdateSettings;
import com.intellij.cvsSupport2.actions.update.UpdateSettingsOnCvsConfiguration;
import com.intellij.cvsSupport2.config.CvsApplicationLevelConfiguration;
import com.intellij.cvsSupport2.config.CvsConfiguration;
import com.intellij.cvsSupport2.config.CvsRootConfiguration;
import com.intellij.cvsSupport2.config.DateOrRevisionSettings;
import com.intellij.cvsSupport2.cvsoperations.common.CompositeOperation;
import com.intellij.cvsSupport2.cvsoperations.cvsCheckOut.CheckoutProjectOperation;
import com.intellij.cvsSupport2.cvsoperations.cvsUpdate.UpdateOperation;
import com.intellij.cvsSupport2.cvsoperations.dateOrRevision.SimpleRevision;
import com.intellij.cvsSupport2.keywordSubstitution.KeywordSubstitutionWrapper;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vcs.FilePath;
import com.intellij.openapi.vcs.FilePathImpl;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlDocument;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;

import java.io.File;

/**
 * This file is part of Project Set Plugin.
 * User: walter
 * Date: 05.04.13
 * Time: 11:42
 * <p/>
 * This is the main action to load project set files.
 */
public class UpdateModulesFromPsfAction extends AnAction
{
  /**
   * Implement this method to provide your action handler.
   *
   * @param event Carries information on the invocation place
   */
  @Override
  public void actionPerformed(AnActionEvent event)
  {
    boolean isAModuleCheckedOut = false;
    CompositeOperation operation = new CompositeOperation();

    XmlFile xmlFile = (XmlFile)LangDataKeys.PSI_FILE.getData(event.getDataContext());

    Project project = event.getProject();

    if (project == null || xmlFile == null)
    {
      // this should not happen
      return;
    }
    
    XmlDocument document = xmlFile.getDocument();
    if (document == null)
    {
      Messages.showErrorDialog(project, "Invalid project set file", "Can't Update Modules");
      return;
    }
    XmlTag rootTag = document.getRootTag();
    if (rootTag == null)
    {
      Messages.showErrorDialog(project, "Invalid project set file", "Can't Update Modules");
      return;
    }
    for (XmlTag xmlTag : rootTag.getSubTags())
    {
      if (xmlTag.getLocalName().equals("provider"))
      {
        String idAttributeValue = xmlTag.getAttributeValue("id", null);
        if (idAttributeValue != null && !idAttributeValue.equals("org.eclipse.team.cvs.core.cvsnature"))
        {
          Messages.showErrorDialog(project, "Unsupported provider in project set file", "Can't Update Modules");
          return;
        }
        else
        {
          for (XmlTag projectTag : xmlTag.getSubTags())
          {
            if (projectTag.getLocalName().equals("project"))
            {
              String projectReference = projectTag.getAttributeValue("reference", null);
              ProjectReference reference = new ProjectReference(projectReference);

              UpdateSettings settings = createSettings(reference);

              Module module = getModule(project, reference);
              if (module != null)
              {
                ModuleRootManager rootManager = ModuleRootManager.getInstance(module);
                VirtualFile[] roots = rootManager.getContentRoots();
                FilePath[] files = new FilePath[roots.length];

                for (int i = 0; i < roots.length; i++)
                {
                  VirtualFile root = roots[i];
                  files[i] = FilePathImpl.create(root);
                }

                operation.addOperation(new UpdateOperation(files, settings, project));
              }
              else
              {
                if (reference.connection.indexOf('@') < 0)
                {
                  Messages.showErrorDialog(project, "For initial check out of modules you need to define a user in the project set file CVS root configurations", "Can't check out module " + reference.remoteModuleName);
                }
                else
                {
                  VirtualFile baseDir = project.getBaseDir();
                  final CvsApplicationLevelConfiguration config = CvsApplicationLevelConfiguration.getInstance();
                  final KeywordSubstitutionWrapper substitution = KeywordSubstitutionWrapper.getValue(config.CHECKOUT_KEYWORD_SUBSTITUTION);
                  CvsRootConfiguration environment = config.getConfigurationForCvsRoot(reference.connection);

                  CheckoutProjectOperation checkoutProjectOperation = new CheckoutProjectOperation(new String[]{reference.remoteModuleName}, new ModifiedCvsEnvironmentProxy(environment, new SimpleRevision(reference.stickyTag)), false, new File(baseDir.getCanonicalPath()), reference.localModuleName, true, substitution == null ? null : substitution.getSubstitution());

                  operation.addOperation(checkoutProjectOperation);
                  isAModuleCheckedOut = true;
                }
              }
            }
          }
        }
      }
    }

    try
    {
      CvsVcs2.executeOperation("Update from project set file", operation, project);

      if (isAModuleCheckedOut)
      {
        Messages.showInfoMessage(project, "Please run import module action to recognize the newly checked out modules.", "New Modules Were Checked Out");
      }
    }
    catch (VcsException e)
    {
      Messages.showErrorDialog(project, "Error while updating: " + e.getMessage(), "Update Error");
    }
  }

  private Module getModule(Project aProject, ProjectReference aReference)
  {
    ModuleManager moduleManager = ModuleManager.getInstance(aProject);
    Module module = moduleManager.findModuleByName(aReference.localModuleName);
    if (module == null)
    {
      // Maybe the module's name is different to the name of the content root -> Let us check all content roots
      for (Module loopModule : moduleManager.getModules())
      {
        ModuleRootManager rootManager = ModuleRootManager.getInstance(loopModule);
        VirtualFile[] roots = rootManager.getContentRoots();
        for (VirtualFile root : roots)
        {
          if (root.getName().equals(aReference.localModuleName))
          {
            module = loopModule;
            break;
          }
        }
      }
    }
    return module;
  }

  private UpdateSettings createSettings(ProjectReference aReference)
  {
    CvsConfiguration configuration = new CvsConfiguration();

    if (aReference.stickyTag == null)
    {
      configuration.RESET_STICKY = true;
    }
    else
    {
      DateOrRevisionSettings settings = configuration.UPDATE_DATE_OR_REVISION_SETTINGS;
      settings.USE_BRANCH = true;
      settings.USE_DATE = false;
      settings.BRANCH = aReference.stickyTag;
    }

    return new UpdateSettingsOnCvsConfiguration(configuration, configuration.CLEAN_COPY, configuration.RESET_STICKY);
  }

  /**
   * Updates the state of the action. Default implementation does nothing.
   * Override this method to provide the ability to dynamically change action's
   * state and(or) presentation depending on the context (For example
   * when your action state depends on the selection you can check for
   * selection and change the state accordingly).
   * This method can be called frequently, for instance, if an action is added to a toolbar,
   * it will be updated twice a second. This means that this method is supposed to work really fast,
   * no real work should be done at this phase. For example, checking selection in a tree or a list,
   * is considered valid, but working with a file system is not. If you cannot understand the state of
   * the action fast you should do it in the {@link #actionPerformed(com.intellij.openapi.actionSystem.AnActionEvent)} method and notify
   * the user that action cannot be executed if it's the case.
   *
   * @param event Carries information on the invocation place and data available
   */
  @Override
  public void update(AnActionEvent event)
  {
    super.update(event);

    Presentation presentation = event.getPresentation();
    VirtualFile virtualFile = LangDataKeys.VIRTUAL_FILE.getData(event.getDataContext());
    PsiFile psiFile = LangDataKeys.PSI_FILE.getData(event.getDataContext());

    if (virtualFile != null && psiFile != null)
    {
      String extension = virtualFile.getExtension();
      if (extension != null)
      {
        boolean flag = psiFile instanceof XmlFile && extension.toUpperCase().equals("PSF");
        presentation.setVisible(flag);
      }
    }
  }
}
