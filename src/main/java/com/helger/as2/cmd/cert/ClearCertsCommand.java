
package com.helger.as2.cmd.cert;

import com.helger.as2.cmd.CommandResult;
import com.helger.as2.cmd.ECommandResultType;
import com.helger.as2lib.cert.IAliasedCertificateFactory;
import com.helger.as2lib.exception.OpenAS2Exception;

public class ClearCertsCommand extends AbstractAliasedCertCommand
{
  @Override
  public String getDefaultDescription ()
  {
    return "Deletes all certificates from the store";
  }

  @Override
  public String getDefaultName ()
  {
    return "clear";
  }

  @Override
  public String getDefaultUsage ()
  {
    return "clear";
  }

  @Override
  public CommandResult execute (final IAliasedCertificateFactory certFx, final Object [] params) throws OpenAS2Exception
  {
    synchronized (certFx)
    {
      certFx.clearCertificates ();

      return new CommandResult (ECommandResultType.TYPE_OK, "cleared");

    }
  }
}
