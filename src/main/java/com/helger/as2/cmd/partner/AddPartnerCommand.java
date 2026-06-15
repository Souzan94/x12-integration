
package com.helger.as2.cmd.partner;

import com.helger.as2.cmd.CommandResult;
import com.helger.as2.cmd.ECommandResultType;
import com.helger.as2lib.exception.OpenAS2Exception;
import com.helger.as2lib.partner.xml.IPartnershipFactoryWithPartners;
import com.helger.as2lib.partner.xml.Partner;
import com.helger.as2lib.partner.xml.XMLPartnershipFactory;
import com.helger.xml.microdom.IMicroDocument;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.MicroDocument;

/**
 * adds a new partner entry in partnership store
 *
 * @author joseph mcverry
 */
public class AddPartnerCommand extends AbstractAliasedPartnershipsCommand
{
  @Override
  public String getDefaultDescription ()
  {
    return "Add a new partner to partnership store.";
  }

  @Override
  public String getDefaultName ()
  {
    return "add";
  }

  @Override
  public String getDefaultUsage ()
  {
    return "add name <attribute 1=value 1> <attribute 2=value 2> ... <attribute n=value n>";
  }

  @Override
  public CommandResult execute (final IPartnershipFactoryWithPartners partFx,
                                final Object [] params) throws OpenAS2Exception
  {
    if (params.length < 1)
    {
      return new CommandResult (ECommandResultType.TYPE_INVALID_PARAM_COUNT, getUsage ());
    }

    final IMicroDocument doc = new MicroDocument ();
    final IMicroElement root = doc.appendElement ("partner");

    for (int i = 0; i < params.length; i++)
    {
      final String param = (String) params[i];
      final int pos = param.indexOf ('=');
      if (i == 0)
      {
        root.setAttribute ("name", param);
      }
      else
        if (pos == 0)
        {
          return new CommandResult (ECommandResultType.TYPE_ERROR, "incoming parameter missing name");
        }
        else
          if (pos > 0)
          {
            root.setAttribute (param.substring (0, pos), param.substring (pos + 1));
          }
          else
            return new CommandResult (ECommandResultType.TYPE_ERROR, "incoming parameter missing value");
    }

    final Partner aNewPartner = ((XMLPartnershipFactory) partFx).loadPartner (root);
    partFx.addPartner (aNewPartner);

    return new CommandResult (ECommandResultType.TYPE_OK);
  }
}
