/* Copyright 2018 The OpenTracing Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.opentracing.contrib.specialagent;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Arrays;

class ConstructorFingerprint extends Fingerprint implements Comparable<ConstructorFingerprint> {
  private static final long serialVersionUID = -6005870987922050364L;

  static ConstructorFingerprint[] recurse(final Constructor<?>[] methods, final int index, final int depth) {
    for (int i = index; i < methods.length; ++i) {
      if (!methods[i].isSynthetic() && !Modifier.isPrivate(methods[i].getModifiers())) {
        final ConstructorFingerprint fingerprint = new ConstructorFingerprint(methods[i]);
        final ConstructorFingerprint[] fingerprints = recurse(methods, i + 1, depth + 1);
        fingerprints[depth] = fingerprint;
        return fingerprints;
      }
    }

    return depth == 0 ? null : new ConstructorFingerprint[depth];
  }

  private final String[] parameterTypes;
  private final String[] exceptionTypes;

  ConstructorFingerprint(final Constructor<?> constructor) {
    this.parameterTypes = Util.getNames(constructor.getParameterTypes());
    this.exceptionTypes = Util.sort(Util.getNames(constructor.getExceptionTypes()));
  }

  public String[] getParameterTypes() {
    return this.parameterTypes;
  }

  public String[] getExceptionTypes() {
    return this.exceptionTypes;
  }

  @Override
  public int compareTo(final ConstructorFingerprint o) {
    final int comparison = Arrays.compare(parameterTypes, o.parameterTypes);
    if (comparison != 0)
      return comparison;

    return Arrays.compare(exceptionTypes, o.exceptionTypes);
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == this)
      return true;

    if (!(obj instanceof ConstructorFingerprint))
      return false;

    final ConstructorFingerprint that = (ConstructorFingerprint)obj;
    if (parameterTypes == null ? that.parameterTypes != null : that.parameterTypes == null || !Arrays.equals(parameterTypes, that.parameterTypes))
      return false;

    if (exceptionTypes == null ? that.exceptionTypes != null : that.exceptionTypes == null || !Arrays.equals(exceptionTypes, that.exceptionTypes))
      return false;

    return true;
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append("(");
    if (parameterTypes != null)
      builder.append(Util.toString(parameterTypes, ", "));

    builder.append(")");
    if (exceptionTypes != null)
      builder.append(" throws ").append(Util.toString(exceptionTypes, ", "));

    builder.append(";");
    return builder.toString();
  }
}