
package com.helger.as2.cmd.partner;

import javax.annotation.Nonnull;

import com.helger.as2.cmd.CommandResult;
import com.helger.as2.cmd.ECommandResultType;
import com.helger.as2lib.exception.OpenAS2Exception;
import com.helger.as2lib.partner.Partnership;
import com.helger.as2lib.partner.xml.IPartnershipFactoryWithPartners;

/**
 * removes a partner entry in partnership store
 *
 * 
 */
public class DeletePartnerCommand extends AbstractAliasedPartnershipsCommand
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
  public CommandResult execute (@Nonnull final IPartnershipFactoryWithPartners partFx,
                                final Object [] aParams) throws OpenAS2Exception
  {
    if (aParams.length < 1)
      return new CommandResult (ECommandResultType.TYPE_INVALID_PARAM_COUNT, getUsage ());

    final String sName = aParams[0].toString ();

    if (!partFx.getAllPartnerNames ().contains (sName))
      return new CommandResult (ECommandResultType.TYPE_ERROR, "Unknown partner name '" + sName + "'");

    for (final Partnership aPartnership : partFx.getAllPartnerships ())
      if (aPartnership.containsReceiverID (sName) || aPartnership.containsSenderID (sName))
      {
        return new CommandResult (ECommandResultType.TYPE_ERROR,
                                  "Can not delete partner '" + sName + "'; it is tied to some partnerships");
      }

    partFx.removePartner (sName);
    return new CommandResult (ECommandResultType.TYPE_OK);
  }
}
