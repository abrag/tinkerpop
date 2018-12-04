/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tinkerpop.gremlin.driver.ser.binary.types;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import org.apache.tinkerpop.gremlin.driver.ser.SerializationException;
import org.apache.tinkerpop.gremlin.driver.ser.binary.DataType;
import org.apache.tinkerpop.gremlin.driver.ser.binary.GraphBinaryReader;
import org.apache.tinkerpop.gremlin.driver.ser.binary.GraphBinaryWriter;

import java.nio.charset.StandardCharsets;

public class StringSerializer extends SimpleTypeSerializer<String> {
    public StringSerializer() {
        super(DataType.STRING);
    }

    @Override
    public String readValue(final ByteBuf buffer, final GraphBinaryReader context) {
        final int length = buffer.readInt();
        return buffer.readCharSequence(length, StandardCharsets.UTF_8).toString();
    }

    @Override
    public ByteBuf writeValue(final String value, final ByteBufAllocator allocator, final GraphBinaryWriter context) {
        final byte[] stringBytes = value.getBytes(StandardCharsets.UTF_8);
        return allocator.buffer(4 + stringBytes.length).writeInt(stringBytes.length).writeBytes(stringBytes);
    }
}
