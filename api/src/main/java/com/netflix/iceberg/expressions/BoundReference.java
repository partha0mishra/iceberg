/*
 * Copyright 2017 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.netflix.iceberg.expressions;

import com.netflix.iceberg.StructLike;
import com.netflix.iceberg.exceptions.ValidationException;
import com.netflix.iceberg.types.Type;
import com.netflix.iceberg.types.Types;
import java.util.List;

public class BoundReference<T> implements Reference {
  private final int fieldId;
  private final Type type;
  private final int pos;

  BoundReference(Types.StructType struct, int fieldId) {
    this.fieldId = fieldId;
    this.pos = find(fieldId, struct);
    this.type = struct.fields().get(pos).type();
  }

  private int find(int fieldId, Types.StructType struct) {
    List<Types.NestedField> fields = struct.fields();
    for (int i = 0; i < fields.size(); i += 1) {
      if (fields.get(i).fieldId() == fieldId) {
        return i;
      }
    }
    throw new ValidationException(
        "Cannot find top-level field id %d in struct: %s", fieldId, struct);
  }

  public Type type() {
    return type;
  }

  public int fieldId() {
    return fieldId;
  }

  public int pos() {
    return pos;
  }

  public T get(StructLike struct) {
    return struct.get(pos, javaType());
  }

  @Override
  public String toString() {
    return String.format("ref(id=%d, pos=%d, type=%s)", fieldId, pos, type);
  }

  @SuppressWarnings("unchecked")
  private Class<T> javaType() {
    return (Class<T>) type.asPrimitiveType().typeId().javaClass();
  }
}
