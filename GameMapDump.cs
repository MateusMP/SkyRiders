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
        }
    }

    private static void DumpGameObject(GameObject gameObject, StreamWriter writer, string indent)
    {
        MeshFilter mf = gameObject.GetComponent<MeshFilter>();
        Transform t = gameObject.GetComponent<Transform>();

        string data = "";
        if (mf) data += "m ";	// Indica presenca de mesh
        if (t && t.childCount == 0)
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
        writer.WriteLine("{0} {1}", indent, f.sharedMesh.name);
    }

    private static void DumpTransform(StreamWriter writer, string indent, Transform t)
    {
        if (!t) return;
        writer.WriteLine("{0} {1} {2} {3} {4} {5} {6} {7} {8} {9}", indent,
                         t.position.x, t.position.y, t.position.z,
                         t.rotation.eulerAngles.x, t.rotation.eulerAngles.y, t.rotation.eulerAngles.z,
                         t.lossyScale.x, t.lossyScale.y, t.lossyScale.z);
    }
}