using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace GarbageCollection
{
    class MemoryAllocationUnit
    {
        private static MemoryAllocationUnit m_mauInstance = new MemoryAllocationUnit(100);
        public static MemoryAllocationUnit GetInstance()
        {
            return m_mauInstance;
        }

        private Expression[] m_aMemory;
        private List<Expression> marked;
        public int CollectedCount;

        private MemoryAllocationUnit(int cMaxObjects)
        {
            m_aMemory = new Expression[cMaxObjects];
            marked = new List<Expression>();
            int i = 0;
            for (i = 0; i < cMaxObjects; i++)
                m_aMemory[i] = null;
        }

        private int FindFreeMemory()
        {
            int i = 0;
            for (i = 0; i < m_aMemory.Length; i++)
                if (m_aMemory[i] == null)
                    return i;
            return -1;
        }
        public SumExpression NewSumExpression()
        {
            int idx = FindFreeMemory();
            if (idx == -1)
                throw new OutOfMemoryException();
            SumExpression exp = new SumExpression();
            m_aMemory[idx] = exp;
            return exp;
        }
        public ProductExpression NewProductExpression()
        {
            int idx = FindFreeMemory();
            if (idx == -1)
                throw new OutOfMemoryException();
            ProductExpression exp = new ProductExpression();
            m_aMemory[idx] = exp;
            return exp;
        }
        public ValueExpression NewValueExpression(int iValue)
        {
            int idx = FindFreeMemory();
            if (idx == -1)
                throw new OutOfMemoryException();
            ValueExpression exp = new ValueExpression(iValue);
            m_aMemory[idx] = exp;
            return exp;
        }

        private void mark(CompoundExpression expression)
        {
            if (expression == null) return;
            if(!marked.Contains(expression)) marked.Add(expression);
            foreach(Expression sub in expression.SubExpressions)
            {
                if(sub is CompoundExpression)
                {
                    mark((CompoundExpression)sub);
                }
                else if(!marked.Contains(sub))
                {
                    marked.Add(sub);
                }
            }
        }

        //implement the Mark & Sweep algorithm here
        public void GarbageCollection(List<Expression> lRootExpressions)
        {
            CollectedCount = 0;
            
            if (lRootExpressions == null) return;

            // mark
            foreach (Expression expression in lRootExpressions)
            {
                if (expression is CompoundExpression)
                {
                    mark((CompoundExpression)expression);
                }
                else
                {
                    marked.Add(expression);
                }
            }

            // sweep
            for (int i = 0; i < m_aMemory.Length; i++)
            {
                if (m_aMemory[i] != null && !m_aMemory[i].Deleted && !marked.Contains(m_aMemory[i]))
                {
                    m_aMemory[i].Delete();
                    m_aMemory[i] = null;
                }
            }
       
            marked.Clear();
        }
    }
}
