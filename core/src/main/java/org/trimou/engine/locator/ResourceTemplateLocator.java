/*
 * Copyright 2014 Guilherme I F L Weizenmann
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
package org.trimou.engine.locator;

import com.google.common.base.Predicate;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trimou.exception.MustacheException;
import org.trimou.exception.MustacheProblem;
import org.trimou.util.Strings;

/**
 * resource template locator. There is a special {@link Builder} for
 * convenience.
 *
 * @author Guilherme I F L Weizenmann
 */
public class ResourceTemplateLocator extends PathTemplateLocator<String> {

  private static final Logger logger = LoggerFactory
          .getLogger(ResourceTemplateLocator.class);

  private final ClassLoader classLoader;

  /**
   *
   * @param priority
   * @param rootPath If null, no templates will be available for precompilation
   */
  public ResourceTemplateLocator(int priority, String rootPath) {
    this(priority, rootPath, Thread.currentThread().getContextClassLoader());
  }

  /**
   *
   * @param priority
   * @param suffix If null, a full template name must be used
   * @param rootPath If null, no templates will be available for precompilation
   */
  public ResourceTemplateLocator(int priority, String rootPath, String suffix) {
    this(priority, rootPath, suffix, Thread.currentThread()
            .getContextClassLoader());
  }

  /**
   *
   * @param priority
   * @param rootPath If null, no templates will be available for precompilation
   * @param classLoader Must not be null
   */
  public ResourceTemplateLocator(int priority, String rootPath,
          ClassLoader classLoader) {
    this(priority, rootPath, null, classLoader);
  }

  /**
   *
   * @param priority
   * @param suffix If null, a full template name must be used
   * @param rootPath If null, no templates will be available for precompilation
   * @param classLoader Must not be null
   */
  public ResourceTemplateLocator(int priority, String rootPath,
          String suffix, ClassLoader classLoader) {
    super(priority, rootPath, suffix);
    if (classLoader != null) {
      this.classLoader = classLoader;
    } else {
      this.classLoader = Thread.currentThread().getContextClassLoader();
    }
  }

  @Override
  public Reader locate(String templatePath) {
    ClassLoader cl = this.classLoader;
    InputStream in = cl.getResourceAsStream(getRootPath()
            + addSuffix(toRealPath(templatePath)));

    if (in == null) {
      return null;
    }
    logger.debug("Template located: {}", templatePath);

    try {
      return new InputStreamReader(in, getDefaultFileEncoding());
    } catch (UnsupportedEncodingException e) {
      throw new MustacheException(MustacheProblem.TEMPLATE_LOADING_ERROR, e);
    }
  }

  @Override
  public Set<String> getAllIdentifiers() {
    Set<String> resources = listResources(getRootPath(), this.classLoader);

    if (resources.isEmpty()) {
      return Collections.emptySet();
    }

    Set<String> identifiers = new HashSet<String>();
    for (String resource : resources) {
      String id = stripSuffix(constructVirtualPath(resource));
      identifiers.add(id);
      logger.debug("Template available: {}", id);
    }
    return identifiers;
  }

  private Set<String> listResources(String path, ClassLoader cl) {
    Set<String> resources = new HashSet<String>();

    if (getRootPath() != null) {
      ConfigurationBuilder confBuilder = new ConfigurationBuilder();
      confBuilder.setScanners(new ResourcesScanner());
      confBuilder.addClassLoader(this.classLoader);
      confBuilder.addUrls(ClasspathHelper.forManifest());
      Reflections reflections = confBuilder.build();

      Pattern pattern;
      if (this.getSuffix() != null) {
        pattern = Pattern.compile(".*\\." + this.getSuffix());
      } else {
        pattern = Pattern.compile(".*");
      }
      System.out.println("PATTERN: " + pattern);
      Set<String> resourcePaths = reflections.getResources(new Predicate<String>() {

        @Override
        public boolean apply(String t) {
          throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
      });
      System.out.println("=========================>>>>>>>>>>>>>>>>>>>>>>");
      System.out.println("=========================>>>>>>>>>>>>>>>>>>>>>>");
      System.out.println(resourcePaths);
      System.out.println("=========================>>>>>>>>>>>>>>>>>>>>>>");
      System.out.println("=========================>>>>>>>>>>>>>>>>>>>>>>");

      if (resourcePaths != null) {
        for (String resourcePath : resourcePaths) {
          if (resourcePath.endsWith(Strings.SLASH)) {
            // Subdirectory
            String subdirectory = getRootPath()
                    + StringUtils.substringAfter(resourcePath,
                            getRootPath());
            resources.addAll(listResources(subdirectory, cl));
          } else {
            if (getSuffix() != null
                    && !resourcePath.endsWith(getSuffix())) {
              continue;
            }
            resources.add(resourcePath);
          }
        }
      }
    }
    return resources;
  }

  private void checkRootPath() {
    if (!getRootPath().startsWith(Strings.SLASH)) {
      throw new MustacheException(
              MustacheProblem.TEMPLATE_LOCATOR_INVALID_CONFIGURATION,
              "Root path does not begin with slash: " + getRootPath());
    }
  }

  @Override
  protected String constructVirtualPath(String source) {
    String[] parts = StringUtils.split(
            StringUtils.substringAfter(source, getRootPath()),
            Strings.SLASH);

    StringBuilder name = new StringBuilder();
    for (int i = 0; i < parts.length; i++) {
      name.append(parts[i]);
      if (i + 1 < parts.length) {
        name.append(getVirtualPathSeparator());
      }
    }
    return name.toString();
  }

  /**
   *
   * @param priority
   * @return a new instance of builder
   */
  public static Builder builder(int priority) {
    return new Builder(priority);
  }

  /**
   *
   * @author Martin Kouba
   */
  public static class Builder {

    private ClassLoader classLoader;

    private int priority;

    private String rootPath;

    private String suffix;

    private Builder(int priority) {
      this.priority = priority;
    }

    /**
     * If not set, TCCL is used.
     *
     * @param classLoader
     * @return builder
     */
    public Builder setClassLoader(ClassLoader classLoader) {
      this.classLoader = classLoader;
      return this;
    }

    /**
     * If not set, no templates will be available for precompilation.
     *
     * @param rootPath
     * @return builder
     */
    public Builder setRootPath(String rootPath) {
      this.rootPath = rootPath;
      return this;
    }

    /**
     * If not set, a full template name must be used.
     *
     * @param suffix
     * @return builder
     */
    public Builder setSuffix(String suffix) {
      this.suffix = suffix;
      return this;
    }

    public ResourceTemplateLocator build() {
      return classLoader != null ? new ResourceTemplateLocator(priority,
              rootPath, suffix, classLoader)
              : new ResourceTemplateLocator(priority, rootPath, suffix);
    }

  }

}
