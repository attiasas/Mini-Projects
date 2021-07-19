package AI_ML_DL.Single_Search_Agent.src.sol;

public class TopSpinPuzzleHeuristic
{
	public double getHeuristic
	(
		TopSpinPuzzleState problemState
	)
	{
		int res = 0;
		for(int i = 0; i < problemState._TopSpinPuzzle.length - 1; i++)
		{

			if(Math.abs(problemState._TopSpinPuzzle[i + 1] - problemState._TopSpinPuzzle[i]) > 1)
			{
				res+= Math.abs(problemState._TopSpinPuzzle[i + 1]-problemState._TopSpinPuzzle[i]);
			}
		}
		if(Math.abs(problemState._TopSpinPuzzle[problemState._TopSpinPuzzle.length - 1] - problemState._TopSpinPuzzle[0]) > 1)
		{
			res+= Math.abs(problemState._TopSpinPuzzle[problemState._TopSpinPuzzle.length - 1]-problemState._TopSpinPuzzle[0]);
		}

		return res - problemState._TopSpinPuzzle.length - 1 <= 0 ? 0 : res - problemState._TopSpinPuzzle.length - 1;
	}
}
