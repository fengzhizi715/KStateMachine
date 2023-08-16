package com.safframework.statemachine.algorithm

import com.safframework.statemachine.state.IState
import com.safframework.statemachine.state.InternalState
import com.safframework.statemachine.state.requireInternalParent

/**
 *
 * @FileName:
 *          com.safframework.statemachine.algorithm.TreeAlgorithm
 * @author: Tony Shen
 * @date: 2023/7/4 14:58
 * @version: V1.0 <描述当前版本功能>
 */
object TreeAlgorithm{

    fun InternalState.findPathFromTargetToLca(targetState: InternalState): MutableList<InternalState> {
        var thisNode = this
        var targetNode = targetState
        var thisDepth = thisNode.findDepth()
        var targetDepth = targetState.findDepth()
        val targetPath = mutableListOf<InternalState>()

        while (thisDepth != targetDepth) {
            if (thisDepth > targetDepth) {
                thisNode = thisNode.requireInternalParent()
                thisDepth--
            } else {
                targetPath.add(targetNode)

                targetNode = targetNode.requireInternalParent()
                targetDepth--
            }
        }

        while (thisNode !== targetNode) {
            thisNode = thisNode.requireInternalParent()

            targetPath.add(targetNode)
            targetNode = targetNode.requireInternalParent()
        }

        targetPath.add(thisNode) // add lca
        return targetPath
    }

    private fun IState.findDepth(): Int {
        var depth = 0
        var parent = this.parent
        while (parent != null) {
            depth++
            parent = parent.parent
        }
        return depth
    }
}