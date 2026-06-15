
package com.helger.as2.cmdprocessor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.as2.cmd.ICommand;
import com.helger.as2.cmd.ICommandRegistry;
import com.helger.as2lib.IDynamicComponent;
import com.helger.as2lib.exception.OpenAS2Exception;
import com.helger.as2lib.session.IAS2Session;
import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.annotation.ReturnsMutableObject;
import com.helger.commons.annotation.UnsupportedOperation;
import com.helger.commons.collection.attr.StringMap;
import com.helger.commons.collection.impl.CommonsLinkedHashMap;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.commons.collection.impl.ICommonsOrderedMap;
import com.helger.commons.lang.ClassHelper;

public abstract class AbstractCommandProcessor implements ICommandProcessor, IDynamicComponent, Runnable
{
  private final StringMap m_aAttrs = new StringMap ();
  private final ICommonsOrderedMap <String, ICommand> m_aCommands = new CommonsLinkedHashMap <> ();
  private volatile boolean m_bTerminated = false;

  public AbstractCommandProcessor ()
  {}

  @Nonnull
  @ReturnsMutableObject
  public final StringMap attrs ()
  {
    return m_aAttrs;
  }

  @Nonnull
  public String getName ()
  {
    return ClassHelper.getClassLocalName (this);
  }

  @UnsupportedOperation
  public IAS2Session getSession ()
  {
    throw new UnsupportedOperationException ("No session available!");
  }

  public void init () throws OpenAS2Exception
  {}

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsMap <String, ICommand> getAllCommands ()
  {
    return m_aCommands.getClone ();
  }

  @Nullable
  public ICommand getCommand (final String name)
  {
    return m_aCommands.get (name);
  }

  public boolean isTerminated ()
  {
    return m_bTerminated;
  }

  @UnsupportedOperation
  public void processCommand () throws OpenAS2Exception
  {
    throw new OpenAS2Exception ("super class method call, not initialized correctly");
  }

  public void addCommands (@Nonnull final ICommandRegistry aCommandRegistry)
  {
    ValueEnforcer.notNull (aCommandRegistry, "CommandRegistry");
    m_aCommands.putAll (aCommandRegistry.getAllCommands ());
  }

  public void terminate ()
  {
    m_bTerminated = true;
  }
}
