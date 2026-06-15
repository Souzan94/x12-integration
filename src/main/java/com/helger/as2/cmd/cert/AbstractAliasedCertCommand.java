
package com.helger.as2.cmd.cert;

import com.helger.as2.cmd.AbstractCommand;
import com.helger.as2.cmd.CommandResult;
import com.helger.as2.cmd.ECommandResultType;
import com.helger.as2lib.cert.IAliasedCertificateFactory;
import com.helger.as2lib.cert.ICertificateFactory;
import com.helger.as2lib.exception.OpenAS2Exception;

public abstract class AbstractAliasedCertCommand extends AbstractCommand
{
  @Override
  public final CommandResult execute (final Object [] params)
  {
    try
    {
      final ICertificateFactory certFx = getSession ().getCertificateFactory ();

      if (certFx instanceof IAliasedCertificateFactory)
        return execute ((IAliasedCertificateFactory) certFx, params);

      return new CommandResult (ECommandResultType.TYPE_COMMAND_NOT_SUPPORTED,
                                "Not supported by current certificate store");
    }
    catch (final OpenAS2Exception oae)
    {
      oae.terminate ();

      return new CommandResult (oae);
    }
  }

  protected abstract CommandResult execute (IAliasedCertificateFactory certFx,
                                            Object [] params) throws OpenAS2Exception;
}
