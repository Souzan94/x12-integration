
package com.helger.as2.cmd.cert;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import javax.annotation.Nonnull;

import com.helger.as2.cmd.CommandResult;
import com.helger.as2.cmd.ECommandResultType;
import com.helger.as2lib.cert.IAliasedCertificateFactory;
import com.helger.as2lib.exception.OpenAS2Exception;
import com.helger.as2lib.exception.WrappedOpenAS2Exception;
import com.helger.as2lib.util.AS2Helper;
import com.helger.commons.io.stream.NonBlockingBufferedInputStream;
import com.helger.security.keystore.EKeyStoreType;
import com.helger.security.keystore.IKeyStoreType;

public class ImportCertCommand extends AbstractAliasedCertCommand
{
  @Override
  public String getDefaultDescription ()
  {
    return "Import a certificate into the current certificate store";
  }

  @Override
  public String getDefaultName ()
  {
    return "import";
  }

  @Override
  public String getDefaultUsage ()
  {
    return "import <alias> <filename> [<password>]";
  }

  @Override
  public CommandResult execute (final IAliasedCertificateFactory certFx, final Object [] params) throws OpenAS2Exception
  {
    if (params.length < 2)
    {
      return new CommandResult (ECommandResultType.TYPE_INVALID_PARAM_COUNT, getUsage ());
    }

    synchronized (certFx)
    {
      final String alias = params[0].toString ();
      final String filename = params[1].toString ();
      String password = null;

      if (params.length > 2)
      {
        password = params[2].toString ();
      }

      try
      {
        if (filename.endsWith (".p12"))
        {
          if (password == null)
          {
            return new CommandResult (ECommandResultType.TYPE_INVALID_PARAM_COUNT,
                                      getUsage () + " (Password is required for p12 files)");
          }

          return importPrivateKey (EKeyStoreType.PKCS12, certFx, alias, filename, password);
        }
        return importCert (certFx, alias, filename);
      }
      catch (final Exception ex)
      {
        throw WrappedOpenAS2Exception.wrap (ex);
      }
    }
  }

  @Nonnull
  protected CommandResult importCert (final IAliasedCertificateFactory certFx,
                                      final String sAlias,
                                      final String sFilename) throws IOException, CertificateException, OpenAS2Exception
  {
    try (final FileInputStream fis = new FileInputStream (sFilename);
        final NonBlockingBufferedInputStream bis = new NonBlockingBufferedInputStream (fis))
    {
      final CertificateFactory cf = CertificateFactory.getInstance ("X.509");
      while (bis.available () > 0)
      {
        final Certificate aCert = cf.generateCertificate (bis);

        if (aCert instanceof X509Certificate)
        {
          certFx.addCertificate (sAlias, (X509Certificate) aCert, true);

          final CommandResult cmdRes = new CommandResult (ECommandResultType.TYPE_OK,
                                                          "Certificate(s) imported successfully");
          cmdRes.addResult ("Imported certificate: " + aCert.toString ());
          return cmdRes;
        }
      }

      return new CommandResult (ECommandResultType.TYPE_ERROR, "No valid X509 certificates found");
    }
  }

  protected CommandResult importPrivateKey (@Nonnull final IKeyStoreType aKeyStoreType,
                                            final IAliasedCertificateFactory aFactory,
                                            final String sAlias,
                                            final String sFilename,
                                            final String sPassword) throws Exception
  {
    final KeyStore aKeyStore = AS2Helper.getCryptoHelper ().createNewKeyStore (aKeyStoreType);
    try (final InputStream aIS = new FileInputStream (sFilename))
    {
      aKeyStore.load (aIS, sPassword.toCharArray ());
    }

    final Enumeration <String> aliases = aKeyStore.aliases ();
    while (aliases.hasMoreElements ())
    {
      final String sCertAlias = aliases.nextElement ();
      final Certificate aCert = aKeyStore.getCertificate (sCertAlias);
      if (aCert instanceof X509Certificate)
      {
        aFactory.addCertificate (sAlias, (X509Certificate) aCert, true);

        final Key certKey = aKeyStore.getKey (sCertAlias, sPassword.toCharArray ());
        aFactory.addPrivateKey (sAlias, certKey, sPassword);

        return new CommandResult (ECommandResultType.TYPE_OK, "Imported certificate and key: " + aCert.toString ());
      }
    }

    return new CommandResult (ECommandResultType.TYPE_ERROR, "No valid X509 certificates found");
  }
}
