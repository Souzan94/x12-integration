
package com.helger.as2.cmd.cert;

import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import com.helger.as2.cmd.CommandResult;
import com.helger.as2.cmd.ECommandResultType;
import com.helger.as2.util.ByteCoder;
import com.helger.as2lib.cert.IAliasedCertificateFactory;
import com.helger.as2lib.exception.OpenAS2Exception;
import com.helger.as2lib.exception.WrappedOpenAS2Exception;
import com.helger.commons.io.stream.NonBlockingByteArrayInputStream;

public class ImportCertInEncodedStreamCommand extends AbstractAliasedCertCommand
{
  @Override
  public String getDefaultDescription ()
  {
    return "Import a certificate into the current certificate store using an encoded byte stream";
  }

  @Override
  public String getDefaultName ()
  {
    return "importbystream";
  }

  @Override
  public String getDefaultUsage ()
  {
    return "importbybstream <alias> <encodedCertificateStream>";
  }

  @Override
  public CommandResult execute (final IAliasedCertificateFactory certFx, final Object [] params) throws OpenAS2Exception
  {
    if (params.length != 2)
    {
      return new CommandResult (ECommandResultType.TYPE_INVALID_PARAM_COUNT, getUsage ());
    }

    synchronized (certFx)
    {
      try
      {
        return _importCert (certFx, params[0].toString (), params[1].toString ());
      }
      catch (final Exception ex)
      {
        throw WrappedOpenAS2Exception.wrap (ex);
      }
    }
  }

  private CommandResult _importCert (final IAliasedCertificateFactory certFx,
                                     final String alias,
                                     final String encodedCert) throws CertificateException, OpenAS2Exception
  {

    final NonBlockingByteArrayInputStream bais = new NonBlockingByteArrayInputStream (ByteCoder.decode (encodedCert)
                                                                                               .getBytes ());
    final CertificateFactory cf = CertificateFactory.getInstance ("X.509");
    while (bais.available () > 0)
    {
      final Certificate cert = cf.generateCertificate (bais);
      if (cert instanceof X509Certificate)
      {
        certFx.addCertificate (alias, (X509Certificate) cert, true);

        final CommandResult cmdRes = new CommandResult (ECommandResultType.TYPE_OK,
                                                        "Certificate(s) imported successfully");
        cmdRes.addResult ("Imported certificate: " + cert.toString ());
        return cmdRes;
      }
    }

    return new CommandResult (ECommandResultType.TYPE_ERROR, "No valid X509 certificates found");
  }
}
