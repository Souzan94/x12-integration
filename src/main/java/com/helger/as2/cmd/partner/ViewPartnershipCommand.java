
package com.helger.as2.cmd.partner;

import com.helger.as2.cmd.CommandResult;
import com.helger.as2.cmd.ECommandResultType;
import com.helger.as2lib.exception.OpenAS2Exception;
import com.helger.as2lib.partner.Partnership;
import com.helger.as2lib.partner.xml.IPartnershipFactoryWithPartners;

/**
 * view the partnership in the partnership stores
 *
 * 
 */
public class ViewPartnershipCommand extends AbstractAliasedPartnershipsCommand
{
  @Override
  public String getDefaultDescription ()
  {
    return "View the partnership entry in partnership store.";
  }

  @Override
  public String getDefaultName ()
  {
    return "view";
  }

  @Override
  public String getDefaultUsage ()
  {
    return "view <name>";
  }

  @Override
  protected CommandResult execute (final IPartnershipFactoryWithPartners partFx,
                                   final Object [] params) throws OpenAS2Exception
  {
    if (params.length < 1)
    {
      return new CommandResult (ECommandResultType.TYPE_INVALID_PARAM_COUNT, getUsage ());
    }

    final String name = params[0].toString ();
    final Partnership part = partFx.getPartnershipByName (name);
    if (part != null)
      return new CommandResult (ECommandResultType.TYPE_OK, part.toString ());

    return new CommandResult (ECommandResultType.TYPE_ERROR, "Unknown partnership name");
  }
}
