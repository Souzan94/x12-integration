
package com.helger.as2.app.session;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.WillClose;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.as2.cmd.CommandManager;
import com.helger.as2.cmd.ICommandRegistry;
import com.helger.as2.cmd.ICommandRegistryFactory;
import com.helger.as2.cmdprocessor.AbstractCommandProcessor;
import com.helger.as2lib.cert.ICertificateFactory;
import com.helger.as2lib.exception.OpenAS2Exception;
import com.helger.as2lib.partner.IPartnershipFactory;
import com.helger.as2lib.processor.IMessageProcessor;
import com.helger.as2lib.processor.module.IProcessorModule;
import com.helger.as2lib.session.AS2Session;
import com.helger.as2lib.util.AS2XMLHelper;
import com.helger.commons.io.file.FileHelper;
import com.helger.commons.string.StringParser;
import com.helger.xml.microdom.IMicroDocument;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.serialize.MicroReader;

/**
 * original author unknown in this release added command registry methods
 *
 *
 */
public class AS2ServerXMLSession extends AS2Session implements ICommandRegistryFactory
{
  public static final String ATTR_CRYPTO_VERIFY_USE_CERTIFICATE_IN_BODY_PART = "CryptoVerifyUseCertificateInBodyPart";
  public static final String ATTR_CRYPTO_SIGN_INCLUDE_CERTIFICATE_IN_BODY_PART = "CryptoSignIncludeCertificateInBodyPart";
  public static final String EL_CERTIFICATES = "certificates";
  public static final String EL_CMDPROCESSOR = "commandProcessors";
  public static final String EL_PROCESSOR = "processor";
  public static final String EL_PARTNERSHIPS = "partnerships";
  public static final String EL_COMMANDS = "commands";

  private static final Logger LOGGER = LoggerFactory.getLogger (AS2ServerXMLSession.class);

  private final String m_sBaseDirectory;
  private final CommandManager m_aCmdManager = CommandManager.getCmdManager ();
  private ICommandRegistry m_aCommandRegistry;

  public AS2ServerXMLSession (@Nonnull final String sFilename) throws OpenAS2Exception, IOException
  {
    this (new File (sFilename).getCanonicalFile ().getAbsoluteFile ());
  }

  public AS2ServerXMLSession (@Nonnull final File aFile) throws OpenAS2Exception
  {
    m_sBaseDirectory = aFile.getParentFile ().getAbsolutePath ();
    load (FileHelper.getInputStream (aFile));
  }

  @Nonnull
  public String getBaseDirectory ()
  {
    return m_sBaseDirectory;
  }

  @Nonnull
  public CommandManager getCommandManager ()
  {
    return m_aCmdManager;
  }

  @Nullable
  public ICommandRegistry getCommandRegistry ()
  {
    return m_aCommandRegistry;
  }

  protected void loadCertificates (@Nonnull final IMicroElement aElement) throws OpenAS2Exception
  {
    LOGGER.info ("  loading certificates");
    final ICertificateFactory certFx = AS2XMLHelper.createComponent (aElement,
                                                                     ICertificateFactory.class,
                                                                     this,
                                                                     m_sBaseDirectory);
    setCertificateFactory (certFx);
  }

  protected void loadCommands (@Nonnull final IMicroElement aElement) throws OpenAS2Exception
  {
    LOGGER.info ("  loading commands");
    final ICommandRegistry cmdReg = AS2XMLHelper.createComponent (aElement,
                                                                  ICommandRegistry.class,
                                                                  this,
                                                                  m_sBaseDirectory);
    m_aCommandRegistry = cmdReg;
  }

  protected void loadCommandProcessors (@Nonnull final IMicroElement aElement) throws OpenAS2Exception
  {
    final List <IMicroElement> aElements = aElement.getAllChildElements ("commandProcessor");
    LOGGER.info ("  loading " + aElements.size () + " command processors");
    for (final IMicroElement processor : aElements)
      loadCommandProcessor (m_aCmdManager, processor);
  }

  protected void loadCommandProcessor (@Nonnull final CommandManager aCommandMgr,
                                       @Nonnull final IMicroElement aElement) throws OpenAS2Exception
  {
    final AbstractCommandProcessor aCmdProcesor = AS2XMLHelper.createComponent (aElement,
                                                                                AbstractCommandProcessor.class,
                                                                                this,
                                                                                m_sBaseDirectory);
    aCommandMgr.addProcessor (aCmdProcesor);
    LOGGER.info ("    loaded command processor " + aCmdProcesor.getName ());
  }

  protected void loadPartnerships (final IMicroElement eRootNode) throws OpenAS2Exception
  {
    LOGGER.info ("  loading partnerships");
    final IPartnershipFactory partnerFx = AS2XMLHelper.createComponent (eRootNode,
                                                                        IPartnershipFactory.class,
                                                                        this,
                                                                        m_sBaseDirectory);
    setPartnershipFactory (partnerFx);
  }

  protected void loadMessageProcessor (final IMicroElement eRootNode) throws OpenAS2Exception
  {
    LOGGER.info ("  loading message processor");
    final IMessageProcessor aMsgProcessor = AS2XMLHelper.createComponent (eRootNode,
                                                                          IMessageProcessor.class,
                                                                          this,
                                                                          m_sBaseDirectory);
    setMessageProcessor (aMsgProcessor);

    for (final IMicroElement eModule : eRootNode.getAllChildElements ("module"))
      loadProcessorModule (aMsgProcessor, eModule);
  }

  protected void loadProcessorModule (@Nonnull final IMessageProcessor aMsgProcessor,
                                      @Nonnull final IMicroElement eModule) throws OpenAS2Exception
  {
    final IProcessorModule aProcessorModule = AS2XMLHelper.createComponent (eModule,
                                                                            IProcessorModule.class,
                                                                            this,
                                                                            m_sBaseDirectory);
    aMsgProcessor.addModule (aProcessorModule);
    LOGGER.info ("    loaded processor module " + aProcessorModule.getName ());
  }

  protected void load (@Nonnull @WillClose final InputStream aIS) throws OpenAS2Exception
  {
    final IMicroDocument aDoc = MicroReader.readMicroXML (aIS);
    final IMicroElement eRoot = aDoc.getDocumentElement ();

    // Special global attributes
    final String sCryptoVerifyUseCertificateInBodyPart = eRoot.getAttributeValue (ATTR_CRYPTO_VERIFY_USE_CERTIFICATE_IN_BODY_PART);
    if (sCryptoVerifyUseCertificateInBodyPart != null)
      setCryptoVerifyUseCertificateInBodyPart (StringParser.parseBool (sCryptoVerifyUseCertificateInBodyPart,
                                                                       DEFAULT_CRYPTO_VERIFY_USE_CERTIFICATE_IN_BODY_PART));

    final String sCryptoSignIncludeCertificateInBodyPart = eRoot.getAttributeValue (ATTR_CRYPTO_SIGN_INCLUDE_CERTIFICATE_IN_BODY_PART);
    if (sCryptoSignIncludeCertificateInBodyPart != null)
      setCryptoSignIncludeCertificateInBodyPart (StringParser.parseBool (sCryptoSignIncludeCertificateInBodyPart,
                                                                         DEFAULT_CRYPTO_SIGN_INCLUDE_CERTIFICATE_IN_BODY_PART));

    for (final IMicroElement eRootChild : eRoot.getAllChildElements ())
    {
      final String sNodeName = eRootChild.getTagName ();

      if (sNodeName.equals (EL_CERTIFICATES))
        loadCertificates (eRootChild);
      else
        if (sNodeName.equals (EL_PROCESSOR))
          loadMessageProcessor (eRootChild);
        else
          if (sNodeName.equals (EL_CMDPROCESSOR))
            loadCommandProcessors (eRootChild);
          else
            if (sNodeName.equals (EL_PARTNERSHIPS))
              loadPartnerships (eRootChild);
            else
              if (sNodeName.equals (EL_COMMANDS))
                loadCommands (eRootChild);
              else
                throw new OpenAS2Exception ("Undefined tag: " + sNodeName);
    }
  }
}
