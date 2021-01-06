package org.simpleframework.module.argument;

import java.io.StringReader;
import java.util.Map;

import junit.framework.TestCase;

public class YamlReaderTest extends TestCase {
   
   private static final String SOURCE =
   "---\n"+
   "# An employee record\n"+
   "name: Martin D'vloper\n"+
   "job: Developer\n"+
   "skill: Elite\n"+
   "employed: True\n"+
   "foods:\n"+
   "    - Apple\n"+
   "    - Orange\n"+
   "    - Strawberry\n"+
   "    - Mango\n"+
   "languages:\n"+
   "    perl: Elite\n"+
   "    python: Elite\n"+
   "    pascal: Lame\n"+
   "education: |\n"+
   "    4 GCSEs\n"+
   "    3 A-Levels\n"+
   "    BSc in the Internet of Things\n";


   public void testYamlParse() throws Exception {
      YamlReader reader = new YamlReader();
      StringReader source = new StringReader(SOURCE);
      Map<String, String> map = reader.read(source);
      
      assertEquals(map.get("job"), "Developer");
      assertEquals(map.get("languages.perl"), "Elite");
      assertEquals(map.get("languages.python"), "Elite");
      assertEquals(map.get("languages.pascal"), "Lame");
   }

   public void testReadEmptyFile() throws Exception {
      YamlReader reader = new YamlReader();
      StringReader source = new StringReader("");
      Map<String, String> map = reader.read(source);

      assertNotNull(map);
      assertTrue(map.isEmpty());
   }
}
