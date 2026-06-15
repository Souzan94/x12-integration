
package com.helger.as2.cmd.cert;

import java.security.cert.Certificate;
import java.util.Map;

import com.helger.as2.cmd.CommandResult;
import com.helger.as2.cmd.ECommandResultType;
import com.helger.as2lib.cert.IAliasedCertificateFactory;
import com.helger.as2lib.exception.OpenAS2Exception;

public class ListCertCommand extends AbstractAliasedCertCommand
{
  @Override
  public String getDefaultDescription ()
  {
    return "List all certificate aliases in the current certificate store";
  }

  @Override
  public String getDefaultName ()
  {
    return "list";
  }

  @Override
  public String getDefaultUsage ()
  {
    return "list";
  }

  @Override
  public CommandResult execute (final IAliasedCertificateFactory certFx, final Object [] params) throws OpenAS2Exception
  {
    synchronized (certFx)
    {
      final Map <String, Certificate> certs = certFx.getCertificates ();
      final CommandResult cmdRes = new CommandResult (ECommandResultType.TYPE_OK);
      for (final String sCertName : certs.keySet ())
        cmdRes.addResult (sCertName);

      if (cmdRes.hasNoResult ())
        cmdRes.addResult ("No certificates available");

      return cmdRes;
    }
  }
}
