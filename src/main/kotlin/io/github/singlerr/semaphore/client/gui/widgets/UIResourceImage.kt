package io.github.singlerr.semaphore.client.gui.widgets

import gg.essential.elementa.components.UIImage
import gg.essential.elementa.components.image.BlurHashImage
import net.minecraft.util.ResourceLocation
import java.util.concurrent.CompletableFuture
import java.util.function.Supplier

open class UIResourceImage(resourceLocation: ResourceLocation, cutRange: Box? = null) :
    UIImage(CompletableFuture.supplyAsync { loadResource(resourceLocation, cutRange) }) {

    constructor(resourceLocSupplier: Supplier<ResourceLocation>) : this(resourceLocSupplier.get())
}

class UIResourceBlurHashImage(
    resourceLocation: ResourceLocation,
    hash: String,
    cutRange: Box? = null
) :
    UIImage(
        CompletableFuture.supplyAsync { loadResource(resourceLocation, cutRange) },
        BlurHashImage(hash)
    )
