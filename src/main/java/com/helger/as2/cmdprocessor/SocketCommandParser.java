
package com.helger.as2.cmdprocessor;

import java.io.CharArrayWriter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.helger.xml.serialize.read.SAXReader;
import com.helger.xml.serialize.read.SAXReaderSettings;

/**
 * used to parse commands from the socket command processor message format
 * &lt;command userid="abc" pasword="xyz"&gt; the actual command&lt;/command&gt;
 *
 * 
 */
public class SocketCommandParser extends DefaultHandler
{
  private String m_sUserID;
  private String m_sPassword;
  private String m_sCommandText;

  /** simple string processor */
  private final CharArrayWriter m_aContents = new CharArrayWriter ();

  /**
   * constructor
   */
  public SocketCommandParser ()
  {}

  public void parse (@Nullable final String sInLine)
  {
    m_sUserID = "";
    m_sPassword = "";
    m_sCommandText = "";
    m_aContents.reset ();

    if (sInLine != null)
    {
      SAXReader.readXMLSAX (sInLine,
                            new SAXReaderSettings ().setEntityResolver (this)
                                                    .setDTDHandler (this)
                                                    .setContentHandler (this)
                                                    .setErrorHandler (this));
    }
  }

  /**
   * Method handles #PCDATA
   *
   * @param ch
   *        array
   * @param start
   *        position in array where next has been placed
   * @param length
   *        int
   */
  @Override
  public void characters (final char [] ch, final int start, final int length)
  {
    m_aContents.write (ch, start, length);
  }

  @Override
  public void startElement (final String sURI,
                            final String sLocalName,
                            final String sQName,
                            final Attributes aAttributes) throws SAXException
  {
    if (sQName.equals ("command"))
    {
      m_sUserID = aAttributes.getValue ("id");
      m_sPassword = aAttributes.getValue ("password");
    }
  }

  @Override
  public void endElement (final String sURI, final String sLocalName, @Nonnull final String sQName) throws SAXException
  {
    if (sQName.equals ("command"))
    {
      m_sCommandText = m_aContents.toString ();
      m_aContents.reset ();
    }
    else
      m_aContents.flush ();
  }

  public String getCommandText ()
  {
    return m_sCommandText;
  }

  public String getPassword ()
  {
    return m_sPassword;
  }

  public String getUserid ()
  {
    return m_sUserID;
  }
}
