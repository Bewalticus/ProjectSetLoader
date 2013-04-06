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

import java.util.StringTokenizer;

/**
 * This file is part of Project Set Loader Plugin.
 * User: walter
 * Date: 05.04.13
 * Time: 13:01
 * <p/>
 * This data object holds the single values from a refernce attribute from the PSF.
 */
public class ProjectReference
{
  public String version;
  public String connection;
  public String remoteModuleName;
  public String localModuleName;
  public String stickyTag;

  public ProjectReference(String aReferenceText)
  {
    parseReferenceText(aReferenceText);
  }

  private void parseReferenceText(String aText)
  {
    StringTokenizer tokenizer = new StringTokenizer(aText, ",");
    version = tokenizer.nextToken();
    connection = tokenizer.nextToken();
    remoteModuleName = tokenizer.nextToken();
    localModuleName = tokenizer.nextToken();
    if (tokenizer.hasMoreTokens())
    {
      stickyTag = tokenizer.nextToken();
      if (stickyTag.equals("HEAD"))
      {
        stickyTag = null;
      }
    }
  }
}
