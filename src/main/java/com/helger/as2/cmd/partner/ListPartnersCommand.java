
package com.helger.as2.cmd.partner;

import com.helger.as2.cmd.CommandResult;
import com.helger.as2.cmd.ECommandResultType;
import com.helger.as2lib.exception.OpenAS2Exception;
import com.helger.as2lib.partner.xml.IPartnershipFactoryWithPartners;

/**
 * list partner entries in partnership store
 *
 * 
 */
public class ListPartnersCommand extends AbstractAliasedPartnershipsCommand
{
  @Override
  public String getDefaultDescription ()
  {
    return "List all partners in the current partnership store";
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
  public CommandResult execute (final IPartnershipFactoryWithPartners partFx,
                                final Object [] params) throws OpenAS2Exception
  {
    final CommandResult cmdRes = new CommandResult (ECommandResultType.TYPE_OK);

    for (final String sPartnerName : partFx.getAllPartnerNames ())
      cmdRes.addResult (sPartnerName);

    if (cmdRes.hasNoResult ())
      cmdRes.addResult ("No partner definitions available");

    return cmdRes;
  }
}
