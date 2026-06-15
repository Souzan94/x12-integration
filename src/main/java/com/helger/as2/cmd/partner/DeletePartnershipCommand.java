
package com.helger.as2.cmd.partner;

import com.helger.as2.cmd.CommandResult;
import com.helger.as2.cmd.ECommandResultType;
import com.helger.as2lib.exception.OpenAS2Exception;
import com.helger.as2lib.partner.Partnership;
import com.helger.as2lib.partner.xml.IPartnershipFactoryWithPartners;

/**
 * removes a partnership entry in partnership store
 *
 * 
 */
public class DeletePartnershipCommand extends AbstractAliasedPartnershipsCommand
{
  @Override
  public String getDefaultDescription ()
  {
    return "Delete the partnership associated with an name.";
  }

  @Override
  public String getDefaultName ()
  {
    return "delete";
  }

  @Override
  public String getDefaultUsage ()
  {
    return "delete <name>";
  }

  @Override
  public CommandResult execute (final IPartnershipFactoryWithPartners partFx,
                                final Object [] params) throws OpenAS2Exception
  {
    if (params.length < 1)
    {
      return new CommandResult (ECommandResultType.TYPE_INVALID_PARAM_COUNT, getUsage ());
    }

    final String name = params[0].toString ();
    final Partnership part = partFx.getPartnershipByName (name);
    if (part != null)
    {
      partFx.removePartnership (part);
      return new CommandResult (ECommandResultType.TYPE_OK, "deleted " + name);
    }

    return new CommandResult (ECommandResultType.TYPE_ERROR, "Unknown partnership name");
  }
}
