
package com.helger.as2.cmd;

import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.as2lib.exception.OpenAS2Exception;
import com.helger.as2lib.session.IAS2Session;
import com.helger.commons.collection.ArrayHelper;
import com.helger.commons.collection.attr.IStringMap;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;

public class MultiCommand extends AbstractCommand
{
  private ICommonsList <ICommand> m_aCmds;

  @Override
  public void initDynamicComponent (@Nonnull final IAS2Session session,
                                    @Nullable final IStringMap parameters) throws OpenAS2Exception
  {
    super.initDynamicComponent (session, parameters);
    getAttributeAsStringRequired (ATTR_NAME);
    getAttributeAsStringRequired (ATTR_DESCRIPTION);
    if (getUsage () == null)
      setUsage (getName () + " <command> <parameters>");
  }

  @Nullable
  public ICommand getCommand (@Nonnull final String sName)
  {
    final String sLCName = sName.toLowerCase (Locale.US);
    for (final ICommand cmd : getCommands ())
      if (cmd.getName ().equals (sLCName))
        return cmd;
    return null;
  }

  @Nonnull
  public ICommonsList <ICommand> getCommands ()
  {
    if (m_aCmds == null)
      m_aCmds = new CommonsArrayList <> ();
    return m_aCmds;
  }

  public String getDescription (final String name)
  {
    final ICommand cmd = getCommand (name);
    return cmd == null ? null : cmd.getDescription ();
  }

  public String getUsage (final String name)
  {
    final ICommand cmd = getCommand (name);
    return cmd == null ? null : cmd.getUsage ();
  }

  @Override
  public CommandResult execute (final Object [] params)
  {
    if (params.length > 0)
    {
      final String subName = params[0].toString ();
      final ICommand subCmd = getCommand (subName);
      if (subCmd != null)
      {
        // All params expcept first
        final Object [] aSubParams = ArrayHelper.getCopy (params, 1, params.length - 1);
        return subCmd.execute (aSubParams);
      }
    }

    final CommandResult listCmds = new CommandResult (ECommandResultType.TYPE_ERROR, "List of valid subcommands:");
    for (final ICommand currentCmd : getCommands ())
      listCmds.addResult (currentCmd.getName ());
    return listCmds;
  }

  public CommandResult execute (final String name, final Object [] params) throws OpenAS2Exception
  {
    final ICommand cmd = getCommand (name);

    if (cmd != null)
      return cmd.execute (params);
    throw new CommandException ("Command doesn't exist: " + name);
  }

  public boolean supports (final String name)
  {
    return getCommand (name) != null;
  }

  @Override
  public String getDefaultName ()
  {
    return null;
  }

  @Override
  public String getDefaultDescription ()
  {
    return null;
  }

  @Override
  public String getDefaultUsage ()
  {
    return null;
  }
}
