
package com.helger.as2.cmdprocessor;

import javax.annotation.Nonnull;

import com.helger.as2.cmd.ICommand;
import com.helger.as2.cmd.ICommandRegistry;
import com.helger.as2lib.exception.OpenAS2Exception;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.impl.ICommonsMap;

public interface ICommandProcessor
{
  @Nonnull
  @ReturnsMutableCopy
  ICommonsMap <String, ICommand> getAllCommands ();

  boolean isTerminated ();

  void addCommands (@Nonnull ICommandRegistry reg);

  void init () throws OpenAS2Exception;

  void terminate ();

  void processCommand () throws OpenAS2Exception;
}
