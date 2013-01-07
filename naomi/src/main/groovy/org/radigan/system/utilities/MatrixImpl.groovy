// @file     Matrix.groovy
// @author   Mac Radigan

package org.radigan.system.utilities

import org.apache.commons.math3.linear.RealMatrix
import org.apache.commons.math3.linear.Array2DRowRealMatrix
import org.apache.commons.math3.linear.EigenDecomposition
import org.apache.commons.math3.linear.MatrixUtils
import org.apache.commons.math3.linear.DecompositionSolver
import org.apache.commons.math3.linear.QRDecomposition
import org.apache.log4j.Logger

public class MatrixImpl implements Matrix {

  RealMatrix m = null

  public MatrixImpl(double[][] data) {
    m = new Array2DRowRealMatrix(data, true)
  }
  public MatrixImpl(Matrix m) {
    this.m = new Array2DRowRealMatrix(m.getData(), true)
  }
  public MatrixImpl(RealMatrix m) {
    this.m = m
  }

  public int getRows() {
    m.getRowDimension()
  }
  public List<Integer> getRowRange() {
    return 0 .. (getRows()-1)
  }
  public int getCols() {
    m.getColumnDimension()
  }
  public List<Integer> getColRange() {
    return 0 .. (getCols()-1)
  }
  public void setEntry(int row, int col, double val) {
    m.setEntry(row, col, val)
  }
  public double getEntry(int row, int col) {
    return m.getEntry(row, col)
  }

  public RealMatrix getUnderlying() {
    return m
  }

  public Matrix inverse() {
    // https://issues.apache.org/jira/browse/MATH-858
    return new MatrixImpl(new QRDecomposition(m).getSolver().getInverse())
  }

  public Matrix pow(int n) {
    if(n==1) {
      return this
    } else if(n>=0) {
      return new MatrixImpl(m.power(n))
    } else {
      return inverse().pow(-1*n)
    }
  }
  public Matrix xor(int n) {
    return pow(n)
  }

  public double eigW(int index) {
    final eigd = new EigenDecomposition(m, 0.001)
    return eigd.getRealEigenvalue(index)
  }

  public Matrix transpose() {
    return new MatrixImpl(m.transpose())
  }
  public Matrix t() {
    return transpose()
  }

  public Matrix mult(Matrix m) {
    return new MatrixImpl(this.m.multiply(m.getUnderlying()))
  }
  public Matrix multiply(Matrix m) {
    return mult(m)
  }

  public List<Integer> size() {
    return [getRows(), getCols()]
  }

  public Matrix multElementwise(Matrix m) {
    def rval = MatrixUtils.createRealMatrix(getRows(), getCols())
    for(int row=0; row<getRows(); row++) {
      for(int col=0; col<getCols(); col++) {
        rval.setEntry(row,col,this.m.getEntry(row,col)*m.getEntry(row,col))
      }
    }
    return new MatrixImpl(rval)
  }
  public Matrix power(Matrix m) {
    return multElementwise(m)
  }

  public Matrix add(Matrix m) {
    return new MatrixImpl(this.m.add(m.getUnderlying()))
  }
  public Matrix plus(Matrix m) {
    return add(m)
  }

  public static Matrix vertcat(List<Matrix> m) {
    return null
  }

  public static Matrix horzcat(List<Matrix> m) {
    return null
  }

  public Matrix gt(double th) {
    def rval = MatrixUtils.createRealMatrix(m.getRowDimension(), m.getColumnDimension())
    for(int row=0; row<m.getRowDimension(); row++) {
      for(int col=0; col<m.getColumnDimension(); col++) {
        if(m.getEntry(row,col)>th) rval.setEntry(row,col,1)
      }
    }
    return new MatrixImpl(rval)
  }
  public Matrix rightShift(double th) {
    return gt(th)
  }

  public Matrix lt(double th) {
    def rval = MatrixUtils.createRealMatrix(m.getRowDimension(), m.getColumnDimension())
    for(int row=0; row<m.getRowDimension(); row++) {
      for(int col=0; col<m.getColumnDimension(); col++) {
        if(m.getEntry(row,col)<th) rval.setEntry(row,col,1)
      }
    }
    return new MatrixImpl(rval)
  }
  public Matrix leftShift(double th) {
    return lt(th)
  }

  public Matrix signum() {
    def rval = MatrixUtils.createRealMatrix(m.getRowDimension(), m.getColumnDimension())
    for(int row=0; row<m.getRowDimension(); row++) {
      for(int col=0; col<m.getColumnDimension(); col++) {
        if(m.getEntry(row,col)>0) rval.setEntry(row,col,+1)
        if(m.getEntry(row,col)<0) rval.setEntry(row,col,-1)
        if(m.getEntry(row,col)==0) rval.setEntry(row,col,0)
      }
    }
    return new MatrixImpl(rval)
  }

  public Matrix abs() {
    def rval = MatrixUtils.createRealMatrix(m.getRowDimension(), m.getColumnDimension())
    for(int row=0; row<m.getRowDimension(); row++) {
      for(int col=0; col<m.getColumnDimension(); col++) {
        rval.setEntry(row,col,m.getEntry(row,col).abs())
      }
    }
    return new MatrixImpl(rval)
  }

  public Matrix fill(Matrix m) {
    def rval = new MatrixImpl(getUnderlying())
    for(int row=0; row<m.getRows(); row++) {
      for(int col=0; col<m.getCols(); col++) {
        rval.setEntry(row,col,m.getEntry(row,col))
      }
    }
    return rval
  }

  public Matrix min() {
    def rval = MatrixUtils.createRealMatrix(1, getCols())
    for(int col=0; col<getCols(); col++) {
      def val = Double.MAX_VALUE
      for(int row=0; row<getRows(); row++) {
        def cur = m.getEntry(row,col)
        if(cur<val) val = cur
      }
      rval.setEntry(0,col,val)
    }
    return new MatrixImpl(rval)
  }
  public Matrix max() {
    def rval = MatrixUtils.createRealMatrix(1, getCols())
    for(int col=0; col<getCols(); col++) {
      def val = Double.MIN_VALUE
      for(int row=0; row<getRows(); row++) {
        def cur = m.getEntry(row,col)
        if(cur>val) val = cur
      }
      rval.setEntry(0,col,val)
    }
    return new MatrixImpl(rval)
  }

  public Matrix sum() {
    def rval = MatrixUtils.createRealMatrix(1, getCols())
    for(int col=0; col<getCols(); col++) {
      def val = 0
      for(int row=0; row<getRows(); row++) {
        val += m.getEntry(row,col)
      }
      rval.setEntry(0,col,val)
    }
    return new MatrixImpl(rval)
  }

  public Matrix land(Matrix a) {
    def rval = MatrixUtils.createRealMatrix(m.getRowDimension(), m.getColumnDimension())
    for(int row=0; row<m.getRowDimension(); row++) {
      for(int col=0; col<m.getColumnDimension(); col++) {
        def mval = m.getEntry(row,col)
        def aval = a.getEntry(row,col)
        mval&&aval ? rval.setEntry(row,col,1) : rval.setEntry(row,col,0)
      }
    }
    return new MatrixImpl(rval)
  }
  public Matrix and(Matrix a) {
    return land(a)
  }

  public List<Integer> find(List<Integer> rows, List<Integer> cols) {
    def rval = new ArrayList<Integer>()
    rows.each { row ->
      cols.each { col ->
        //def ind = row*(getCols()-1)+col
        def ind = row*getCols()+col
        if(m.getEntry(row,col)) rval << ind
      }
    }
    return rval
  }
  public List<Integer> find() {
    def rval = new ArrayList<Integer>()
    def ind = 0
    for(int row=0; row<getRows(); row++) {
      for(int col=0; col<getCols(); col++) {
        if(m.getEntry(row,col)) rval << ind
        ind++
      }
    }
    return rval
  }
  public List<Integer> indToRows(List<Integer> ind) {
    return ind.collect{(int)Math.floor(it/getRows())}
  }
  public List<Integer> indToCols(List<Integer> ind) {
    return ind.collect{(int)Math.floor(it%getCols())}
  }

  public String toString() {
    def sb = new StringBuffer()
    for(int row=0; row<m.getRowDimension(); row++) {
      for(int col=0; col<m.getColumnDimension(); col++) {
        sb << String.format('%1$04.2f ', m.getEntry(row,col)).padLeft(6)
      }
      sb << String.format('%n')
    }
    return sb.toString()
  }

  public int lastNonzeroRow() {
    def last = 0
    for(int row=0; row<m.getRowDimension(); row++) {
      def sum = 0
      for(int col=0; col<m.getColumnDimension(); col++) {
        sum += m.getEntry(row,col).abs()
      }
      if(sum) last = row
    }
    return last
  }

  public static Matrix eye(int n) {
    return new MatrixImpl(MatrixUtils.createRealIdentityMatrix(n))
  }
  public static Matrix eye(Matrix a) {
    def rval = new MatrixImpl(MatrixUtils.createRealMatrix(a.getRows(), a.getCols()))
    for(int d=0; d<rval.getRows(); d++) {
      rval.setEntry(d,d,1)
    }
    return rval
  }

  public static Matrix zeros(int m, int n) {
    return new MatrixImpl(MatrixUtils.createRealMatrix(m,n))
  }
  public static Matrix zeros(Matrix a) {
    return new MatrixImpl(MatrixUtils.createRealMatrix(a.getRows(), a.getCols()))
  }

  public List getRow(int n) {
    return m.getRowVector(n).toArray()
  }
  public List getCol(int n) {
    return m.getColumnVector(n).toArray()
  }

  public Matrix solve(Matrix b) {
    return new MatrixImpl(new QRDecomposition(m).getSolver().solve(b))
  }
  public Matrix divide(Matrix b) {
    return solve(b)
  }

  public List<Matrix> qr(Matrix a) {
    def qrd = new QRDecomposition(m)
    def rval = []
    return rval << qrd.getQ() << qrd.getR()
  }

  public Matrix sub(List<Integer> rows, List<Integer> cols) {
    return new MatrixImpl(m.getSubMatrix(rows.toArray() as int[],cols.toArray() as int[]))
  }
  public Matrix filter(List<Integer> rows, List<Integer> cols) {
    def nrows = getRowRange().removeAll(rows)
    def ncols = getColRange().removeAll(cols)
    def rval = MatrixUtils.createRealMatrix(nrows.size(), ncols.size())
    nrows.eachWithIndex { row, rind ->
      ncols.eachWithIndex { col, cind ->
        def val = m.getEntry(row,col)
        rval.setEntry(rind,cind,val)
      }
    }
    return new MatrixImpl(rval)
  }

}

// *EOF*
