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

import com.intellij.cvsSupport2.connections.CvsEnvironment;
import com.intellij.cvsSupport2.connections.login.CvsLoginWorker;
import com.intellij.cvsSupport2.cvsoperations.dateOrRevision.RevisionOrDate;
import com.intellij.cvsSupport2.javacvsImpl.io.ReadWriteStatistics;
import com.intellij.openapi.project.Project;
import org.netbeans.lib.cvsclient.CvsRoot;
import org.netbeans.lib.cvsclient.command.CommandException;
import org.netbeans.lib.cvsclient.connection.IConnection;

/**
 * This file is part of Project Set Loader Plugin.
 * User: walter
 * Date: 05.04.13
 * Time: 21:42
 * <p/>
 * This is a proxy class for a CvsEnvironment to inject a RevisionOrDate object. So a specific revision (tag) or date can be checked out. 
 */
public class ModifiedCvsEnvironmentProxy implements CvsEnvironment
{
  private final RevisionOrDate iRevisionOrDate;
  private final CvsEnvironment iCvsEnvironment;

  public ModifiedCvsEnvironmentProxy(CvsEnvironment aCvsEnvironment, RevisionOrDate aRevisionOrDate)
  {
    iCvsEnvironment = aCvsEnvironment;
    iRevisionOrDate = aRevisionOrDate;
  }

  @Override
  public IConnection createConnection(ReadWriteStatistics statistics)
  {
    return iCvsEnvironment.createConnection(statistics);
  }

  @Override
  public String getCvsRootAsString()
  {
    return iCvsEnvironment.getCvsRootAsString();
  }

  @Override
  public CvsLoginWorker getLoginWorker(Project project)
  {
    return iCvsEnvironment.getLoginWorker(project);
  }

  @Override
  public RevisionOrDate getRevisionOrDate()
  {
    return iRevisionOrDate;
  }

  @Override
  public String getRepository()
  {
    return iCvsEnvironment.getRepository();
  }

  @Override
  public CvsRoot getCvsRoot()
  {
    return iCvsEnvironment.getCvsRoot();
  }

  @Override
  public boolean isValid()
  {
    return iCvsEnvironment.isValid();
  }

  @Override
  public CommandException processException(CommandException t)
  {
    return iCvsEnvironment.processException(t);
  }

  @Override
  public boolean isOffline()
  {
    return iCvsEnvironment.isOffline();
  }
}
