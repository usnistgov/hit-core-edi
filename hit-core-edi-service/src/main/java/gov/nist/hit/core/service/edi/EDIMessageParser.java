/**
 * This software was developed at the National Institute of Standards and Technology by employees of
 * the Federal Government in the course of their official duties. Pursuant to title 17 Section 105
 * of the United States Code this software is not subject to copyright protection and is in the
 * public domain. This is an experimental system. NIST assumes no responsibility whatsoever for its
 * use by other parties, and makes no guarantees, expressed or implied, about its quality,
 * reliability, or any other characteristic. We would appreciate acknowledgement if the software is
 * used. This software can be redistributed and/or modified freely provided that any derivative
 * works bear some notice that they are derived from it, and any modified versions bear some notice
 * that they have been modified.
 */
package gov.nist.hit.core.service.edi;

import gov.nist.hit.core.domain.MessageModel;
import gov.nist.hit.core.domain.MessageParserCommand;
import gov.nist.hit.core.domain.TestContext;
import gov.nist.hit.core.edi.domain.EDITestContext;
import gov.nist.hit.core.service.MessageParser;
import gov.nist.hit.core.service.exception.MessageParserException;


/**
 * 
 * @author Harold Affo
 * 
 */
public abstract class EDIMessageParser implements MessageParser {

  /**  
   * 
   */
  @Override
  public MessageModel parse(TestContext context, MessageParserCommand command)
      throws MessageParserException {
    try {
      if (context instanceof EDITestContext) {
        EDITestContext testContext = (EDITestContext) context;
        return new MessageModel();
      } else {
        throw new MessageParserException(
            "Invalid Context Provided. Expected Context is HL7V2TestContext but found "
                + context.getClass().getSimpleName());
      }

    } catch (RuntimeException e) {
      throw new MessageParserException(e.getMessage());
    } catch (Exception e) {
      throw new MessageParserException(e.getMessage());
    }
  }


}
