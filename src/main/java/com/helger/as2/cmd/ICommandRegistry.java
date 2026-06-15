
package com.helger.as2.cmd;

import javax.annotation.Nonnull;

import com.helger.as2lib.IDynamicComponent;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.impl.ICommonsOrderedMap;

public interface ICommandRegistry extends IDynamicComponent
{
  @Nonnull
  @ReturnsMutableCopy
  ICommonsOrderedMap <String, ICommand> getAllCommands ();
}
