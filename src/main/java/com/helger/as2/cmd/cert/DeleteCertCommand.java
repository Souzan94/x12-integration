
package com.helger.as2.cmd.cert;

import com.helger.as2.cmd.CommandResult;
import com.helger.as2.cmd.ECommandResultType;
import com.helger.as2lib.cert.IAliasedCertificateFactory;
import com.helger.as2lib.exception.OpenAS2Exception;

public class DeleteCertCommand extends AbstractAliasedCertCommand
{
  @Override
  public String getDefaultDescription ()
  {
    return "Delete the certificate and private key associated with an alias.";
  }

  @Override
  public String getDefaultName ()
  {
    return "delete";
  }

  @Override
  public String getDefaultUsage ()
  {
    return "delete <alias>";
  }

  @Override
  public CommandResult execute (final IAliasedCertificateFactory certFx, final Object [] params) throws OpenAS2Exception
  {
    if (params.length < 1)
    {
      return new CommandResult (ECommandResultType.TYPE_INVALID_PARAM_COUNT, getUsage ());
    }

    final String alias = params[0].toString ();
    synchronized (certFx)
    {
      certFx.removeCertificate (alias);
    }
    return new CommandResult (ECommandResultType.TYPE_OK, "deleted " + alias);
  }
}
