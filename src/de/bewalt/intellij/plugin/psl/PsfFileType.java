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

import com.intellij.icons.AllIcons;
import com.intellij.ide.highlighter.XmlLikeFileType;
import com.intellij.lang.xml.XMLLanguage;
import com.intellij.openapi.fileTypes.FileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * This file is part of Project Set Loader Plugin.
 * User: walter
 * Date: 26.03.13
 * Time: 17:41
 * <p/>
 * This file type for PSF files associates them with XML language.
 */
public class PsfFileType extends XmlLikeFileType
{
  public static FileType INSTANCE = new PsfFileType();

  public PsfFileType()
  {
    super(XMLLanguage.INSTANCE);
  }

  /**
   * Returns the name of the file type. The name must be unique among all file types registered in the system.
   *
   * @return The file type name.
   */
  @NotNull
  @Override
  public String getName()
  {
    return "PSF";
  }

  /**
   * Returns the user-readable description of the file type.
   *
   * @return The file type description.
   */
  @NotNull
  @Override
  public String getDescription()
  {
    return "Project Set File";
  }

  /**
   * Returns the default extension for files of the type.
   *
   * @return The extension, not including the leading '.'.
   */
  @NotNull
  @Override
  public String getDefaultExtension()
  {
    return "psf";
  }

  /**
   * Returns the icon used for showing files of the type.
   *
   * @return The icon instance, or null if no icon should be shown.
   */
  @Nullable
  @Override
  public Icon getIcon()
  {
    return AllIcons.FileTypes.Xml;
  }
}
