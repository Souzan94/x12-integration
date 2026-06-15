
package com.helger.as2.cmd.partner;

import javax.annotation.Nonnull;

import com.helger.as2.cmd.AbstractCommand;
import com.helger.as2.cmd.CommandResult;
import com.helger.as2lib.exception.OpenAS2Exception;
import com.helger.as2lib.partner.IPartnershipFactory;
import com.helger.as2lib.partner.xml.IPartnershipFactoryWithPartners;

public abstract class AbstractAliasedPartnershipsCommand extends AbstractCommand
{
  @Nonnull
  protected abstract CommandResult execute (@Nonnull IPartnershipFactoryWithPartners aPartnershipFactory,
                                            Object [] params) throws OpenAS2Exception;

  @Nonnull
  public final CommandResult execute (final Object [] params)
  {
    try
    {
      final IPartnershipFactory aPartnershipFactory = getSession ().getPartnershipFactory ();
      return execute ((IPartnershipFactoryWithPartners) aPartnershipFactory, params);
    }
    catch (final OpenAS2Exception oae)
    {
      oae.terminate ();
      return new CommandResult (oae);
    }
  }
}
