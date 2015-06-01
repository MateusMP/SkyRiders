using StreamWriter = System.IO.StreamWriter;

using UnityEngine;
using UnityEditor;

public static class GameMapDump
{
    [MenuItem("Debug/Dump Scene")]
    public static void DumpScene()
    {
        if ((Selection.gameObjects == null) || (Selection.gameObjects.Length == 0))
        {
            Debug.LogError("Please select the object(s) you wish to dump.");
            return;
        }

        var path = EditorUtility.SaveFilePanel("Save selected Objects", "", "island" + ".txt", "txt");

        Debug.Log("Path :" + path);

        if (path.Length != 0)
        {
            Debug.Log("Dumping scene to " + path + " ...");
            using (StreamWriter writer = new StreamWriter(path, false))
            {
                foreach (GameObject gameObject in Selection.gameObjects)
                {
                    DumpGameObject(gameObject, writer, "");
                }
            }
            Debug.Log("Scene dumped to " + path);
            
            var thing = EditorUtility.DisplayDialog("Dump Assets", "It Worked !!", "Uhul !");
        }
    }

    private static void DumpGameObject(GameObject gameObject, StreamWriter writer, string indent)
    {
        MeshFilter mf = gameObject.GetComponent<MeshFilter>();
        Transform t = gameObject.GetComponent<Transform>();

        string data = "";
        if (mf) data += "m ";	// Indica presenca de mesh
        if (t)
        { // Indica presenca de transform
            data += "t";
        }
        else
        {
            t = null;
        }

        string name = gameObject.name.Replace(" ", "");

        writer.WriteLine("{0}+ {1} {2} !", indent, name, data);
        //writer.WriteLine("{0}+'{1}' {2} !", indent, gameObject.name, data);

        DumpMesh(writer, indent + "  ", mf);
        DumpTransform(writer, indent + "  ", t);

        foreach (Transform child in gameObject.transform)
        {
            DumpGameObject(child.gameObject, writer, indent + "  ");
        }
    }

    private static void DumpMesh(StreamWriter writer, string indent, MeshFilter f)
    {
        if (!f) return;
		string path = AssetDatabase.GetAssetPath(f.sharedMesh);
        writer.WriteLine("{0} {1} {2}", indent, path, f.sharedMesh.name);
    }

    private static void DumpTransform(StreamWriter writer, string indent, Transform t)
    {
		if (!t) return;
		Vector3 position = t.position;
		// position.z = -position.z;
		//Quaternion rotation = new Quaternion(-t.rotation.x, t.rotation.y, t.rotation.z, -t.rotation.z);
		//position = rotation * position;
		
		Quaternion rotation = t.rotation;
		
		// position = Quaternion.Euler(rotation.eulerAngles.x, rotation.eulerAngles.y+180, rotation.eulerAngles.z) * position;
		
        writer.WriteLine("{0} {1} {2} {3} {4} {5} {6} {7} {8} {9}", indent,
                         -position.x, position.y, position.z,
                         rotation.eulerAngles.x, -rotation.eulerAngles.y, -rotation.eulerAngles.z,
                         t.lossyScale.x, t.lossyScale.y, t.lossyScale.z);
    }
}