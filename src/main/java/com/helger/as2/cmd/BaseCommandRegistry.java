
package com.helger.as2.cmd;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.as2lib.AbstractDynamicComponent;
import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.impl.CommonsLinkedHashMap;
import com.helger.commons.collection.impl.ICommonsOrderedMap;

public class BaseCommandRegistry extends AbstractDynamicComponent implements ICommandRegistry
{
  private static final Logger LOGGER = LoggerFactory.getLogger (BaseCommandRegistry.class);
  private final ICommonsOrderedMap <String, ICommand> m_aCommands = new CommonsLinkedHashMap <> ();

  public void addCommand (@Nonnull final ICommand aCommand)
  {
    ValueEnforcer.notNull (aCommand, "Command");
    final String sCommandName = aCommand.getName ();
    if (m_aCommands.containsKey (sCommandName))
      LOGGER.warn ("Overwriting command '" + sCommandName + "'");
    m_aCommands.put (sCommandName, aCommand);
  }

  public void clearCommands ()
  {
    m_aCommands.clear ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsOrderedMap <String, ICommand> getAllCommands ()
  {
    return m_aCommands.getClone ();
  }
}
