
package com.helger.as2.cmd.partner;

import com.helger.as2.cmd.CommandResult;
import com.helger.as2.cmd.ECommandResultType;
import com.helger.as2lib.exception.OpenAS2Exception;
import com.helger.as2lib.partner.xml.IPartnershipFactoryWithPartners;

/**
 * list partnerships in partnership store by names
 *
 * 
 */
public class ListPartnershipsCommand extends AbstractAliasedPartnershipsCommand
{
  @Override
  public String getDefaultDescription ()
  {
    return "List all partnerships in the current partnership store";
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
    for (final String sName : partFx.getAllPartnershipNames ())
      cmdRes.addResult (sName);

    if (cmdRes.hasNoResult ())
      cmdRes.addResult ("No partnerships available");

    return cmdRes;
  }
}
