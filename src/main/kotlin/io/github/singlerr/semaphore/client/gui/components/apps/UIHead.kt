package io.github.singlerr.semaphore.client.gui.components.apps

import io.github.singlerr.semaphore.client.gui.widgets.Box
import io.github.singlerr.semaphore.client.gui.widgets.UIResourceImage
import io.github.singlerr.semaphore.client.gui.widgets.getPlayerSkin
import java.util.UUID

class UIHead(id: UUID) : UIResourceImage(getPlayerSkin(id), cutRange = Box(8, 8, 8, 8))
