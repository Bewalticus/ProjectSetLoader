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

import com.intellij.openapi.fileTypes.FileTypeConsumer;
import com.intellij.openapi.fileTypes.FileTypeFactory;
import org.jetbrains.annotations.NotNull;

/**
 * This file is part of Project Set Loader Plugin.
 * User: walter
 * Date: 26.03.13
 * Time: 17:39
 * <p/>
 * This defines the file type factory for PSF files to associate them with XML language.
 */
public class ProjectSetFileTypeFactory extends FileTypeFactory
{
  @Override
  public void createFileTypes(@NotNull FileTypeConsumer consumer)
  {
    consumer.consume(PsfFileType.INSTANCE, "psf");
  }
}
