
package com.helger.as2.cmd.partner;

import com.helger.as2.cmd.AbstractCommand;
import com.helger.as2.cmd.CommandResult;
import com.helger.as2.cmd.ECommandResultType;
import com.helger.as2lib.exception.OpenAS2Exception;
import com.helger.as2lib.partner.IPartnershipFactory;
import com.helger.as2lib.partner.xml.XMLPartnershipFactory;

/**
 * replaces the partnership store, backs up the original store
 *
 * 
 */
public class StorePartnershipsCommand extends AbstractCommand
{
  @Override
  public String getDefaultDescription ()
  {
    return "Stores the current partnerships in storage";
  }

  @Override
  public String getDefaultName ()
  {
    return "store";
  }

  @Override
  public String getDefaultUsage ()
  {
    return "store";
  }

  @Override
  public CommandResult execute (final Object [] params)
  {

    try
    {
      final IPartnershipFactory partnerFx = getSession ().getPartnershipFactory ();
      if (partnerFx instanceof XMLPartnershipFactory)
      {
        ((XMLPartnershipFactory) partnerFx).storePartnership ();

        return new CommandResult (ECommandResultType.TYPE_OK, "Stored partnerships");
      }
      return new CommandResult (ECommandResultType.TYPE_COMMAND_NOT_SUPPORTED,
                                "Not supported by current partnership store, must be XML");
    }
    catch (final OpenAS2Exception oae)
    {
      oae.terminate ();

      return new CommandResult (oae);
    }
  }
}
