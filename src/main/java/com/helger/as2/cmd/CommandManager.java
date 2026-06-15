
package com.helger.as2.cmd;

import javax.annotation.Nonnull;

import com.helger.as2.cmdprocessor.AbstractCommandProcessor;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;

/**
 * command calls the registered command processors
 *
 *
 */
public class CommandManager
{
  private static CommandManager s_aDefaultManager;

  private ICommonsList <AbstractCommandProcessor> m_aProcessors;

  @Nonnull
  public static CommandManager getCmdManager ()
  {
    if (s_aDefaultManager == null)
      s_aDefaultManager = new CommandManager ();
    return s_aDefaultManager;
  }

  public void setProcessors (final ICommonsList <AbstractCommandProcessor> aProcessors)
  {
    m_aProcessors = aProcessors;
  }

  public ICommonsList <AbstractCommandProcessor> getProcessors ()
  {
    if (m_aProcessors == null)
      m_aProcessors = new CommonsArrayList <> ();
    return m_aProcessors;
  }

  public void addProcessor (final AbstractCommandProcessor processor)
  {
    getProcessors ().add (processor);
  }
}
