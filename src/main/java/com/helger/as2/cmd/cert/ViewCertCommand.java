
package com.helger.as2.cmd.cert;

import java.security.cert.X509Certificate;

import com.helger.as2.cmd.CommandResult;
import com.helger.as2.cmd.ECommandResultType;
import com.helger.as2lib.cert.IAliasedCertificateFactory;
import com.helger.as2lib.exception.OpenAS2Exception;

/**
 * view certs by alias
 *
 * 
 */
public class ViewCertCommand extends AbstractAliasedCertCommand
{
  @Override
  public String getDefaultDescription ()
  {
    return "View the certificate associated with an alias.";
  }

  @Override
  public String getDefaultName ()
  {
    return "view";
  }

  @Override
  public String getDefaultUsage ()
  {
    return "view <alias>";
  }

  @Override
  protected CommandResult execute (final IAliasedCertificateFactory certFx,
                                   final Object [] params) throws OpenAS2Exception
  {
    if (params.length < 1)
      return new CommandResult (ECommandResultType.TYPE_INVALID_PARAM_COUNT, getUsage ());

    synchronized (certFx)
    {
      final String sAlias = params[0].toString ();
      final X509Certificate cert = certFx.getCertificate (sAlias);
      return new CommandResult (ECommandResultType.TYPE_OK, cert.toString ());
    }
  }
}
