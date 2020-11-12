package com.stepanov.bbf.bugfinder.executor

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.TreeElement
import com.stepanov.bbf.bugfinder.util.getAllParentsWithoutNode
import com.stepanov.bbf.bugfinder.util.removeArbitraryChild
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.psiUtil.siblings


abstract class Checker {

    abstract fun checkCompiling(file: KtFile): Boolean
    abstract fun checkTextCompiling(text: String): Boolean


    fun replacePSINodeIfPossible(file: KtFile, node: PsiElement, replacement: PsiElement): Boolean =
        replaceNodeIfPossible(file, node.node, replacement.node)

    fun replaceNodeIfPossible(file: KtFile, node: ASTNode, replacement: ASTNode): Boolean {
        if (node.text.isEmpty() || node == replacement) return checkCompiling(file)
        for (p in node.getAllParentsWithoutNode()) {
            try {
                if (node.treeParent.elementType.index == DUMMY_HOLDER_INDEX) continue
                val oldText = file.text
                val replCopy = replacement.copyElement()
                if ((node as TreeElement).treeParent !== p) {
                    continue
                }
                p.replaceChild(node, replCopy)
                if (oldText == file.text)
                    continue
                if (!checkCompiling(file)) {
                    p.replaceChild(replCopy, node)
                    return false
                } else {
                    return true
                }
            } catch (e: Error) {
            }
        }
        return false
    }

    fun addNodeIfPossible(file: KtFile, anchor: PsiElement, node: PsiElement, before: Boolean = false): Boolean {
        if (node.text.isEmpty() || node == anchor) return checkCompiling(
            file
        )
        try {
            val addedNode =
                if (before) anchor.parent.addBefore(node, anchor)
                else anchor.parent.addAfter(node, anchor)
            if (checkCompiling(file)) return true
            file.node.removeChild(addedNode.node)
            return false
        } catch (e: Throwable) {
            return false
        }
    }

    fun addNodeIfPossible(file: KtFile, anchor: ASTNode, node: ASTNode, before: Boolean = false): Boolean =
        addNodeIfPossible(file, anchor.psi, node.psi, before)

    fun removeNodeIfPossible(file: KtFile, node: ASTNode): Boolean {
        if (node.text.isEmpty()) return checkCompiling(file)
        try {
            val parent = node.treeParent
            val next = node.treeNext
            parent.removeChild(node)
            if (checkCompiling(file)) return true
            parent.addChild(node, next)
            return false
        } catch (e: Throwable) {
            return false
        }
    }

    fun removeNodeIfPossible(file: KtFile, node: PsiElement): Boolean =
        removeNodeIfPossible(file, node.node)

    private val DUMMY_HOLDER_INDEX: Short = 86
}

