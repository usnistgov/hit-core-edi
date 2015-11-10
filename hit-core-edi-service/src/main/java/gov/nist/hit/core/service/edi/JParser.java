package gov.nist.hit.core.service.edi;

import hl7.v2.instance.Message;
import edi.ncpdp.script.parser.impl.DefaultNCPDPParser;
import edi.ncpdp.script.parser.impl.DefaultNCPDPParser$class;
import scala.util.Try;

public class JParser implements DefaultNCPDPParser {

  /**
   * A java friendly way to call the `parse` method of the default parser implementation
   * 
   * @param message - The message as a string
   * @param model - The message model
   * @return The message instance model encapsulated in a scala `scala.util.Try`
   */
  @SuppressWarnings("unchecked")
  public Try<Message> parse(String message, hl7.v2.profile.Message model) {
    return DefaultNCPDPParser$class.parse(this,message, model);
  }


  /**
   * Call JParser.parse method and decapsulate the result
   */
  public Message jparse(String message, hl7.v2.profile.Message model) throws Exception {
    return parse(message, model).get();
  }

  public String findId(String s1, String s2, int n){
    return "";
  }

    
}
