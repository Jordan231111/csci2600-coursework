// Helper function for sequences.
// Precondition ensures safe indexing, and decreases clause proves termination.
function sumProdSeq(a: seq<int>, b: seq<int>, j: int): int 
  requires 0 <= j < |a|
  requires |a| == |b|
  decreases j
{
  if j == 0 then a[0] * b[0]
  else sumProdSeq(a, b, j - 1) + a[j] * b[j]
}

method dot(a: seq<int>, b: seq<int>) returns (c: seq<int>)
  requires |a| == |b| && |a| > 0
  ensures |c| == |a|
  ensures c[0] == a[0] * b[0]
  ensures forall k :: 0 < k < |a| ==> c[k] == a[k] * b[k] + c[k - 1]
{
  var n := |a|;
  c := [a[0] * b[0]];
  var i := 1;
  while i < n
    invariant 1 <= i <= n
    invariant |c| == i
    invariant c[0] == a[0] * b[0]
    invariant forall j :: 0 <= j < i ==> c[j] == sumProdSeq(a, b, j)
    decreases n - i
  {
    var temp := c[i - 1] + a[i] * b[i];
    c := c + [temp];
    i := i + 1;
  }
}
