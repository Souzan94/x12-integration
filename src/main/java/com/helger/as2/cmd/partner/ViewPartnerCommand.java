
package com.helger.as2.cmd.partner;

import com.helger.as2.cmd.CommandResult;
import com.helger.as2.cmd.ECommandResultType;
import com.helger.as2lib.exception.OpenAS2Exception;
import com.helger.as2lib.partner.xml.IPartner;
import com.helger.as2lib.partner.xml.IPartnershipFactoryWithPartners;

/**
 * view the partner entries in the partnership store
 *
 *
 */
public class ViewPartnerCommand extends AbstractAliasedPartnershipsCommand
{
  @Override
  public String getDefaultDescription ()
  {
    return "View the partner entry in the partnership store.";
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
      return new CommandResult (ECommandResultType.TYPE_INVALID_PARAM_COUNT, getUsage ());

    final String name = params[0].toString ();
    final IPartner aPartner = partFx.getPartnerOfName (name);
    if (aPartner != null)
    {
      final String out = name + "\n" + aPartner.toString ();
      return new CommandResult (ECommandResultType.TYPE_OK, out);
    }

    return new CommandResult (ECommandResultType.TYPE_ERROR, "Unknown partner name");
  }
}
