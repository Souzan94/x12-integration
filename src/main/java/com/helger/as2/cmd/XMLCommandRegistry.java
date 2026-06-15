
package com.helger.as2.cmd;

import java.io.File;
import java.io.InputStream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.as2.app.session.AS2ServerXMLSession;
import com.helger.as2lib.exception.OpenAS2Exception;
import com.helger.as2lib.session.IAS2Session;
import com.helger.as2lib.util.AS2XMLHelper;
import com.helger.commons.collection.attr.IStringMap;
import com.helger.commons.io.file.FileHelper;
import com.helger.xml.microdom.IMicroDocument;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.serialize.MicroReader;

public class XMLCommandRegistry extends BaseCommandRegistry
{
  public static final String ATTR_FILENAME = "filename";

  @Override
  public void initDynamicComponent (@Nonnull final IAS2Session aSession,
                                    @Nullable final IStringMap aParameters) throws OpenAS2Exception
  {
    super.initDynamicComponent (aSession, aParameters);

    refresh ();
  }

  protected void loadCommand (final IMicroElement eCommand,
                              @Nullable final MultiCommand aParent) throws OpenAS2Exception
  {
    final IAS2Session aSession = getSession ();
    final String sBaseDirectory = aSession instanceof AS2ServerXMLSession ? ((AS2ServerXMLSession) aSession).getBaseDirectory ()
                                                                          : null;
    final ICommand aCommand = AS2XMLHelper.createComponent (eCommand, ICommand.class, aSession, sBaseDirectory);
    if (aParent != null)
      aParent.getCommands ().add (aCommand);
    else
      addCommand (aCommand);
  }

  protected void loadMultiCommand (@Nonnull final IMicroElement aCommand,
                                   @Nullable final MultiCommand parent) throws OpenAS2Exception
  {
    final MultiCommand cmd = new MultiCommand ();
    cmd.initDynamicComponent (getSession (), AS2XMLHelper.getAllAttrsWithLowercaseName (aCommand));

    if (parent != null)
      parent.getCommands ().add (cmd);
    else
      addCommand (cmd);

    for (final IMicroElement aChildElement : aCommand.getAllChildElements ())
    {
      final String sChildName = aChildElement.getNodeName ();

      if (sChildName.equals ("command"))
        loadCommand (aChildElement, cmd);
      else
        if (sChildName.equals ("multicommand"))
          loadMultiCommand (aChildElement, cmd);
        else
          throw new OpenAS2Exception ("Undefined child tag: " + sChildName);
    }
  }

  public void load (@Nonnull final InputStream in) throws OpenAS2Exception
  {
    final IMicroDocument aDoc = MicroReader.readMicroXML (in);
    final IMicroElement eRoot = aDoc.getDocumentElement ();

    clearCommands ();

    for (final IMicroElement eElement : eRoot.getAllChildElements ())
    {
      final String sNodeName = eElement.getTagName ();
      if (sNodeName.equals ("command"))
        loadCommand (eElement, null);
      else
        if (sNodeName.equals ("multicommand"))
          loadMultiCommand (eElement, null);
        else
          throw new OpenAS2Exception ("Undefined tag: " + sNodeName);
    }
  }

  public void refresh () throws OpenAS2Exception
  {
    final String sFilename = getAttributeAsStringRequired (ATTR_FILENAME);
    load (FileHelper.getInputStream (new File (sFilename)));
  }
}
