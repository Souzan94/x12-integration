
package com.helger.as2.app.cert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.annotation.DevelopersNote;

@Deprecated
@DevelopersNote ("Use ServerCertificateFactory instead")
public class ServerPKCS12CertificateFactory extends ServerCertificateFactory
{
  private static final Logger LOGGER = LoggerFactory.getLogger (ServerPKCS12CertificateFactory.class);

  static
  {
    LOGGER.warn ("ServerPKCS12CertificateFactory is deprecated. Please use the more generic ServerCertificateFactory instead!");
  }

  public ServerPKCS12CertificateFactory ()
  {}
}
