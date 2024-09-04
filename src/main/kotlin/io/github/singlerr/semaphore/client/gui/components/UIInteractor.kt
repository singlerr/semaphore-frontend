package io.github.singlerr.semaphore.client.gui.components

import io.github.singlerr.semaphore.interactors.admin.presenter.EntityPresenter
import io.github.singlerr.semaphore.interactors.admin.presenter.data.ErrorEntity
import io.github.singlerr.semaphore.interactors.admin.presenter.data.PresentableEntity
import io.github.singlerr.semaphore.interactors.callee.presenter.CallResponsePresenter
import io.github.singlerr.semaphore.interactors.callee.presenter.data.CallResponse
import io.github.singlerr.semaphore.interactors.caller.presenter.CallRequestPresenter
import io.github.singlerr.semaphore.interactors.caller.presenter.data.InverseCallRequest

interface UIInteractor : EntityPresenter, CallRequestPresenter, CallResponsePresenter {

    fun shouldPresent(entity: PresentableEntity?): Boolean = false
    fun shouldPresent(entities: List<PresentableEntity>?): Boolean = false
    fun shouldPresent(error: ErrorEntity?): Boolean = false
    fun shouldPresent(request: InverseCallRequest?): Boolean = false
    fun shouldPresent(entity: CallResponse?): Boolean = false
    fun shouldPresent(entity: Error?): Boolean = false

    override fun presentError(error: ErrorEntity?) {}
    override fun present(entity: PresentableEntity?) {}
    override fun present(entities: MutableList<PresentableEntity>?) {}
    override fun present(request: InverseCallRequest?) {}
    override fun present(entity: CallResponse?) {}
    override fun error(
        entity: io.github.singlerr.semaphore.interactors.callee.presenter.data.Error?
    ) {}
}
