package io.github.singlerr.semaphore.client.gui

import gg.essential.elementa.font.FontRenderer
import gg.essential.elementa.font.data.Font
import net.minecraft.util.ResourceLocation
import java.util.UUID

// Images
val IMAGE_BACKGROUND = ResourceLocation(MOD_ID, "textures/gui/phone_frame_bar.png")

// Icons
val ICON_MISS_CALL = ResourceLocation(MOD_ID, "textures/gui/icons/icon_miss_call.png")
val ICON_REQUEST_CALL = ResourceLocation(MOD_ID, "textures/gui/icons/icon_request_call.png")
val ICON_ADDRESS_BOOK = ResourceLocation(MOD_ID, "textures/gui/icons/icon_address_book.png")
val ICON_USER_REGISTRATION =
    ResourceLocation(MOD_ID, "textures/gui/icons/icon_user_registration.png")
val ICON_SETTINGS = ResourceLocation(MOD_ID, "textures/gui/icons/icon_settings.png")
val ICON_ACCEPT_CALL = ResourceLocation(MOD_ID, "textures/gui/icons/icon_request_call.png")
val ICON_REJECT_CALL = ResourceLocation(MOD_ID, "textures/gui/icons/icon_reject_call.png")

// Shaders
val SHADER_PASS_THROUGH = ResourceLocation(MOD_ID, "shader/passthrough.vsh")
val SHADER_BLURRED_GRADIENT = ResourceLocation(MOD_ID, "shader/blurred_gradient.fsh")

// Fonts
val BMJUA_FONT = Font.fromResource("/fonts/BMJUA")
val BMJUA: FontRenderer = FontRenderer(BMJUA_FONT)

val isDevMode = System.getProperty("devMode", "false").toBoolean()

fun getInternalBackground(id: UUID): ResourceLocation =
    ResourceLocation(MOD_ID, "textures/gui/backgrounds/${id}.png")
