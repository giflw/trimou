/*
 * Copyright 2013 Martin Kouba
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
package org.trimou.exception;

/**
 * Ordinal of this enum should not be used - we don't keep the ordering between
 * versions.
 *
 * @author Martin Kouba
 */
public enum MustacheProblem implements ProblemCode {

    // Compilation problems
    COMPILE_INVALID_DELIMITERS,
    COMPILE_INVALID_TAG,
    COMPILE_INVALID_SECTION_END,
    COMPILE_INVALID_TEMPLATE,
    COMPILE_IO_ERROR,
    COMPILE_HELPER_VALIDATION_FAILURE,
    // Template related problems
    TEMPLATE_NOT_READY,
    TEMPLATE_MODIFICATION_NOT_ALLOWED,
    TEMPLATE_LOCATOR_INVALID_CONFIGURATION,
    TEMPLATE_LOADING_ERROR,
    // Rendering problems
    RENDER_INVALID_PARTIAL_KEY,
    RENDER_INVALID_EXTEND_KEY,
    RENDER_IO_ERROR,
    RENDER_REFLECT_INVOCATION_ERROR,
    RENDER_NO_VALUE,
    RENDER_TEMPLATE_INVOCATION_RECURSIVE_LIMIT_EXCEEDED,
    RENDER_HELPER_INVALID_OPTIONS,
    RENDER_HELPER_INVALID_POP_OPERATION,
    RENDER_GENERIC_ERROR,
    RENDER_ASYNC_PROCESSING_ERROR,
    // Configuration problems
    CONFIG_PROPERTY_INVALID_VALUE, ;

}
