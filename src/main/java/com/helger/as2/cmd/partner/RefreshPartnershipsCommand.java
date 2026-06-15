
package com.helger.as2.cmd.partner;

import com.helger.as2.cmd.AbstractCommand;
import com.helger.as2.cmd.CommandResult;
import com.helger.as2.cmd.ECommandResultType;
import com.helger.as2lib.exception.OpenAS2Exception;
import com.helger.as2lib.partner.IPartnershipFactory;
import com.helger.as2lib.partner.IRefreshablePartnershipFactory;

/**
 * 
 */
public class RefreshPartnershipsCommand extends AbstractCommand
{
  @Override
  public String getDefaultDescription ()
  {
    return "Refresh the current partnerships from storage";
  }

  @Override
  public String getDefaultName ()
  {
    return "refresh";
  }

  @Override
  public String getDefaultUsage ()
  {
    return "refresh";
  }

  @Override
  public CommandResult execute (final Object [] params)
  {
    try
    {
      final IPartnershipFactory partnerFx = getSession ().getPartnershipFactory ();
      if (partnerFx instanceof IRefreshablePartnershipFactory)
      {
        ((IRefreshablePartnershipFactory) partnerFx).refreshPartnershipFactory ();

        return new CommandResult (ECommandResultType.TYPE_OK, "Refreshed partnerships");
      }
      return new CommandResult (ECommandResultType.TYPE_COMMAND_NOT_SUPPORTED,
                                "Not supported by current certificate store");
    }
    catch (final OpenAS2Exception oae)
    {
      oae.terminate ();
      return new CommandResult (oae);
    }
  }
}
