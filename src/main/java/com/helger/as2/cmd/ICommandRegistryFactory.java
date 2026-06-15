
package com.helger.as2.cmd;

import javax.annotation.Nullable;

public interface ICommandRegistryFactory
{
  /**
   * @return The command registry. May be <code>null</code> if it was not yet
   *         initialized.
   */
  @Nullable
  ICommandRegistry getCommandRegistry ();
}
