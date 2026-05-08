/*

   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package org.apache.batik.util;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class ParsedURLDataProtocolHandlerTestCase {
    @Test
    public void testParseErrorWithNoDataUri() {
        String svgContent = "<svg xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" " +
                "width=\"1418\" height=\"370\">" +
                "<image xlink:href=\"data:example.com," +
                "%3Csvg%20xmlns%3D%22http%3A//www.w3.org/2000/svg%22%20xmlns%3Axlink%3D%22http%3A//www.w3.org/1999/xlink" +
                "%22%20width%3D%2250%22%20height%3D%2250%22%3E%3Cimage%20xlink%3Ahref%3D%22http%3A//example.com/s/favicon.png" +
                "%22%20width%3D%2250%22%20height%3D%2250%22/%3E%3C/svg%3E\" width=\"200\" height=\"200\"/></svg>\n";
        PNGTranscoder transcoder = new PNGTranscoder();
        TranscoderInput input = new TranscoderInput(new ByteArrayInputStream(svgContent.getBytes()));
        ByteArrayOutputStream ostream = new ByteArrayOutputStream();
        TranscoderOutput output = new TranscoderOutput(ostream);
        TranscoderException ex = Assert.assertThrows(TranscoderException.class, () -> transcoder.transcode(input, output));
        Assert.assertEquals("null\n" +
                "Enclosed Exception:\n" +
                "The document references a external resource (http://example.com/s/favicon.png) " +
                "which comes from different location than the document itself. This is not allowed " +
                "for security reasons and that resource will not be loaded.", ex.getMessage());
    }
}
