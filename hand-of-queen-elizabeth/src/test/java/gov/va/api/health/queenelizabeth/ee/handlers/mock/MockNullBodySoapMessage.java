package gov.va.api.health.queenelizabeth.ee.handlers.mock;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import javax.xml.soap.*;

/** Mock class to forcefully return null when calling getSOAPBody(). */
public class MockNullBodySoapMessage extends SOAPMessage {

  @Override
  public void addAttachmentPart(AttachmentPart attachmentPart) {}

  @Override
  public int countAttachments() {
    return 0;
  }

  @Override
  public AttachmentPart createAttachmentPart() {
    return null;
  }

  @Override
  public AttachmentPart getAttachment(SOAPElement element) throws SOAPException {
    return null;
  }

  @Override
  public Iterator<AttachmentPart> getAttachments() {
    return null;
  }

  @Override
  public Iterator<AttachmentPart> getAttachments(MimeHeaders headers) {
    return null;
  }

  @Override
  public String getContentDescription() {
    return null;
  }

  @Override
  public void setContentDescription(String description) {}

  @Override
  public MimeHeaders getMimeHeaders() {
    return null;
  }

  @Override
  public SOAPBody getSOAPBody() throws SOAPException {
    return null;
  }

  @Override
  public SOAPPart getSOAPPart() {
    return null;
  }

  @Override
  public void removeAllAttachments() {}

  @Override
  public void removeAttachments(MimeHeaders headers) {}

  @Override
  public void saveChanges() throws SOAPException {}

  @Override
  public boolean saveRequired() {
    return false;
  }

  @Override
  public void writeTo(OutputStream out) throws SOAPException, IOException {}
}
