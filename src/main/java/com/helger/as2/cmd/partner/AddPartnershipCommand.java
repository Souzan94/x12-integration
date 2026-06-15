
package com.helger.as2.cmd.partner;

import com.helger.as2.cmd.CommandResult;
import com.helger.as2.cmd.ECommandResultType;
import com.helger.as2lib.exception.OpenAS2Exception;
import com.helger.as2lib.partner.Partnership;
import com.helger.as2lib.partner.xml.IPartnershipFactoryWithPartners;
import com.helger.as2lib.partner.xml.XMLPartnershipFactory;
import com.helger.xml.microdom.IMicroDocument;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.MicroDocument;

/**
 * adds a new partnership entry in partneship store
 *
 * 
 */
public class AddPartnershipCommand extends AbstractAliasedPartnershipsCommand
{
  @Override
  public String getDefaultDescription ()
  {
    return "Add a new partnership definition to partnership store.";
  }

  @Override
  public String getDefaultName ()
  {
    return "add";
  }

  @Override
  public String getDefaultUsage ()
  {
    return "add name senderId receiverId <attribute 1=value 1> <attribute 2=value 2> ... <attribute n=value n>";
  }

  @Override
  public CommandResult execute (final IPartnershipFactoryWithPartners partFx,
                                final Object [] params) throws OpenAS2Exception
  {
    if (params.length < 3)
      return new CommandResult (ECommandResultType.TYPE_INVALID_PARAM_COUNT, getUsage ());

    final IMicroDocument doc = new MicroDocument ();
    final IMicroElement root = doc.appendElement ("partnership");

    for (int nIndex = 0; nIndex < params.length; nIndex++)
    {
      final String param = (String) params[nIndex];
      final int pos = param.indexOf ('=');
      if (nIndex == 0)
      {
        root.setAttribute ("name", param);
      }
      else
        if (nIndex == 1)
        {
          final IMicroElement elem = root.appendElement ("sender");
          elem.setAttribute ("name", param);
        }
        else
          if (nIndex == 2)
          {
            final IMicroElement elem = root.appendElement ("receiver");
            elem.setAttribute ("name", param);
          }
          else
            if (pos == 0)
            {
              return new CommandResult (ECommandResultType.TYPE_ERROR, "incoming parameter missing name");
            }
            else
              if (pos > 0)
              {
                final IMicroElement elem = root.appendElement ("attribute");
                elem.setAttribute ("name", param.substring (0, pos));
                elem.setAttribute ("value", param.substring (pos + 1));
              }
              else
                return new CommandResult (ECommandResultType.TYPE_ERROR, "incoming parameter missing value");

    }

    final XMLPartnershipFactory aXMLPartnershipFactory = (XMLPartnershipFactory) partFx;
    final Partnership aPartnership = aXMLPartnershipFactory.loadPartnership (root,
                                                                             aXMLPartnershipFactory.getPartnerMap ());
    if (aXMLPartnershipFactory.getPartnershipByName (aPartnership.getName ()) != null)
      return new CommandResult (ECommandResultType.TYPE_ERROR,
                                "A partnership with name '" + aPartnership.getName () + "' is already present!");

    // add the partnership to the list of available partnerships
    partFx.addPartnership (aPartnership);

    return new CommandResult (ECommandResultType.TYPE_OK);
  }
}
