/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.usp.icmc.vicg.gl.jwavefront;

import Handlers.TextureHandler;
import MathClasses.BoundingBox;
import MathClasses.Vector3;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.opengl.GL3;

/**
 *
 * @author PC
 */
public class JWavefrontObject {

  private GL3 gl;
  private ArrayList<Group> groups;
  private ArrayList<Vertex> vertices;
  private ArrayList<Normal> normals;
  private ArrayList<Material> materials;
  private ArrayList<Texture> textures;
  private ArrayList<TextureCoord> textures_coord;
  private File pathname;
  
  public ArrayList<Vertex> getVertices(){
      return vertices;
  }

  /**
   * Construct a JWavefrontObject object.
   *
   * @param file The file containing the object.
   * @param shader
   * @throws IOException
   */
  public JWavefrontObject(File file) {
    groups = new ArrayList<Group>();
    vertices = new ArrayList<Vertex>();
    normals = new ArrayList<Normal>();
    textures_coord = new ArrayList<TextureCoord>();
    materials = new ArrayList<Material>();
    textures = new ArrayList<Texture>();

    pathname = file;
  }

  public void init(GL3 gl) throws IOException {
    this.gl = gl;

    parse(pathname);
  }

  /**
   * "unitize" a model by translating it to the origin and scaling it to fit in
   * a unit cube around the origin.
   *
   * @return Returns the scalefactor used.
   */
  public void unitize() {
    assert (vertices != null);

    float maxx, minx, maxy, miny, maxz, minz;
    float cx, cy, cz, w, h, d;
    float scale;

    /*
     * get the max/mins
     */
    maxx = minx = vertices.get(0).x;
    maxy = miny = vertices.get(0).y;
    maxz = minz = vertices.get(0).z;

    for (int i = 1; i < vertices.size(); i++) {
      if (maxx < vertices.get(i).x) {
        maxx = vertices.get(i).x;
      }
      if (minx > vertices.get(i).x) {
        minx = vertices.get(i).x;
      }

      if (maxy < vertices.get(i).y) {
        maxy = vertices.get(i).y;
      }
      if (miny > vertices.get(i).y) {
        miny = vertices.get(i).y;
      }

      if (maxz < vertices.get(i).z) {
        maxz = vertices.get(i).z;
      }
      if (minz > vertices.get(i).z) {
        minz = vertices.get(i).z;
      }
    }

    /*
     * calculate model width, height, and depth
     */
    w = Math.abs(maxx) + Math.abs(minx);
    h = Math.abs(maxy) + Math.abs(miny);
    d = Math.abs(maxz) + Math.abs(minz);

    /*
     * calculate center of the model
     */
    cx = (maxx + minx) / 2.0f;
    cy = (maxy + miny) / 2.0f;
    cz = (maxz + minz) / 2.0f;

    /*
     * calculate unitizing scale factor
     */
    scale = 2.0f / Math.max(Math.max(w, h), d);

    /*
     * translate around center then scale
     */
    for (int i = 0; i < vertices.size(); i++) {
      vertices.get(i).x -= cx;
      vertices.get(i).y -= cy;
      vertices.get(i).z -= cz;
      vertices.get(i).x *= scale;
      vertices.get(i).y *= scale;
      vertices.get(i).z *= scale;
    }
  }

  /**
   * Reads a model description from a Wavefront.
   *
   * @param file The file containing the Wavefront model.
   * @throws IOException
   */
  private void parse(File file) throws IOException {
    BufferedReader in = null;
    StringTokenizer tok, tok2;
    String token;
    String line = null;
    int id = 0;
    
    Group current_group = null;

    try {
      in = new BufferedReader(new FileReader(file));

      while ((line = in.readLine()) != null) {
        line = line.trim();

        if (line.length() > 0) {
          switch (line.charAt(0)) {
            case '#': /* comment */

              break;
            case 'v': /* v, vn, vt */

              switch (line.charAt(1)) {
                case ' ': /* vertex */

                  tok = new StringTokenizer(line, " ");
                  tok.nextToken(); //ignores v
                  Vertex v = new Vertex(id++,
                          Float.parseFloat(tok.nextToken()),
                          Float.parseFloat(tok.nextToken()),
                          Float.parseFloat(tok.nextToken()));
                  vertices.add(v);
                  break;
                case 'n': /* normal */

                  tok = new StringTokenizer(line, " ");
                  tok.nextToken(); //ignores vn
                  Normal n = new Normal(Float.parseFloat(tok.nextToken()),
                          Float.parseFloat(tok.nextToken()),
                          Float.parseFloat(tok.nextToken()));
                  normals.add(n);
                  break;
                case 't': /* texcoord */

                  tok = new StringTokenizer(line, " ");
                  tok.nextToken(); //ignores vt
                  TextureCoord tc = new TextureCoord(Float.parseFloat(tok.nextToken()),
                          Float.parseFloat(tok.nextToken()));
                  textures_coord.add(tc);
                  break;
                default:
                  Logger.getLogger(JWavefrontObject.class.getName()).log(Level.WARNING,
                          "parse() error: line not recognized >> " + line, file.getName());

              }
              break;
            case 'm': /* mtllib */

              tok = new StringTokenizer(line, " ");
              token = tok.nextToken(); //ignores mtllib

              if (token.equals("mtllib")) {
                token = tok.nextToken();
                parse_mtl(token);
              } else {
                Logger.getLogger(JWavefrontObject.class.getName()).log(Level.WARNING,
                        "parse() error: line not recognized >> " + line, file.getName());
              }
              break;
            case 'u': /* usemtl */

              tok = new StringTokenizer(line, " ");
              token = tok.nextToken();

              if (token.equals("usemtl")) {
                token = tok.nextToken();

                if (current_group == null) {
                  current_group = findGroup(Group.default_group.name);

                  if (current_group == null) {
                    current_group = Group.default_group;
                    groups.add(current_group);
                  }
                }

                Material aux = findMaterial(token);

                if (current_group.material != aux
                        && current_group.material != Material.default_material) {
                  //when changing material inside a group, I
                  //I have to create a new group                                    
                  current_group = new Group("group_" + groups.size());
                  groups.add(current_group);
                }

                current_group.material = aux;
              } else {
                Logger.getLogger(JWavefrontObject.class.getName()).log(Level.WARNING,
                        "parse() error: line not recognized >> " + line, file.getName());
              }
              break;
            case 'g':
            case 'o': /* group */

              tok = new StringTokenizer(line, " ");
              tok.nextToken(); //ignores g
              
              if (tok.hasMoreTokens()) {
                token = tok.nextToken();
                                
                current_group = findGroup(token);
               
                if (current_group == null) {
                  current_group = new Group(token);
                  groups.add(current_group);
                }
              } else {
                current_group = findGroup(Group.default_group.name);

                if (current_group == null) {
                  current_group = Group.default_group;
                  groups.add(current_group);
                }
              }
              break;
            case 'f': /* face */

              //getting the current group
              if (current_group == null) {
                current_group = findGroup(Group.default_group.name);

                if (current_group == null) {
                  current_group = Group.default_group;
                  groups.add(current_group);
                }
              }

              boolean hastexture = (current_group.material.texture != null);

              line = line.trim().substring(1).trim(); //removing f

              if (line.contains("//")) { /* v//n */

                Triangle tri = new Triangle();
                tok = new StringTokenizer(line, " ");

                tok2 = new StringTokenizer(tok.nextToken(), "/");
                tri.vertices[0] = vertices.get(Integer.parseInt(tok2.nextToken())-1);
                tri.vertex_normals[0] = normals.get(Integer.parseInt(tok2.nextToken())-1);

                tok2 = new StringTokenizer(tok.nextToken(), "/");
                tri.vertices[1] = vertices.get(Integer.parseInt(tok2.nextToken())-1);
                tri.vertex_normals[1] = normals.get(Integer.parseInt(tok2.nextToken())-1);

                tok2 = new StringTokenizer(tok.nextToken(), "/");
                tri.vertices[2] = vertices.get(Integer.parseInt(tok2.nextToken())-1);
                tri.vertex_normals[2] = normals.get(Integer.parseInt(tok2.nextToken())-1);

                current_group.triangles.add(tri);

                while (tok.hasMoreTokens()) {
                  Triangle new_tri = new Triangle();
                  tok2 = new StringTokenizer(tok.nextToken(), "/");

                  new_tri.vertices[0] = tri.vertices[0];
                  new_tri.vertices[1] = tri.vertices[2];
                  new_tri.vertices[2] = vertices.get(Integer.parseInt(tok2.nextToken())-1);

                  new_tri.vertex_normals[0] = tri.vertex_normals[0];
                  new_tri.vertex_normals[1] = tri.vertex_normals[2];
                  new_tri.vertex_normals[2] = normals.get(Integer.parseInt(tok2.nextToken())-1);

                  current_group.triangles.add(new_tri);
                  tri = new_tri;
                }
              } else {
                Triangle tri = new Triangle();
                tok = new StringTokenizer(line, " ");
                tok2 = new StringTokenizer(tok.nextToken(), "/");

                if (tok2.countTokens() == 3) { /* v/t/n */

                  tri.vertices[0] = vertices.get(Integer.parseInt(tok2.nextToken())-1);
                  if (hastexture) {
                    tri.vertex_tex_coords[0] = textures_coord.get(Integer.parseInt(tok2.nextToken())-1);
                  }
                  tri.vertex_normals[0] = normals.get(Integer.parseInt(tok2.nextToken())-1);

                  tok2 = new StringTokenizer(tok.nextToken(), "/");
                  tri.vertices[1] = vertices.get(Integer.parseInt(tok2.nextToken())-1);
                  if (hastexture) {
                    tri.vertex_tex_coords[1] = textures_coord.get(Integer.parseInt(tok2.nextToken())-1);
                  }
                  tri.vertex_normals[1] = normals.get(Integer.parseInt(tok2.nextToken())-1);

                  tok2 = new StringTokenizer(tok.nextToken(), "/");
                  tri.vertices[2] = vertices.get(Integer.parseInt(tok2.nextToken())-1);
                  if (hastexture) {
                    tri.vertex_tex_coords[2] = textures_coord.get(Integer.parseInt(tok2.nextToken())-1);
                  }
                  tri.vertex_normals[2] = normals.get(Integer.parseInt(tok2.nextToken())-1);

                  current_group.triangles.add(tri);

                  while (tok.hasMoreTokens()) {
                    Triangle new_tri = new Triangle();
                    tok2 = new StringTokenizer(tok.nextToken(), "/");

                    new_tri.vertices[0] = tri.vertices[0];
                    new_tri.vertices[1] = tri.vertices[2];
                    new_tri.vertices[2] = vertices.get(Integer.parseInt(tok2.nextToken())-1);

                    if (hastexture) {
                      new_tri.vertex_tex_coords[0] = tri.vertex_tex_coords[0];
                      new_tri.vertex_tex_coords[1] = tri.vertex_tex_coords[2];
                      new_tri.vertex_tex_coords[2] = textures_coord.get(Integer.parseInt(tok2.nextToken())-1);
                    }

                    new_tri.vertex_normals[0] = tri.vertex_normals[0];
                    new_tri.vertex_normals[1] = tri.vertex_normals[2];
                    new_tri.vertex_normals[2] = normals.get(Integer.parseInt(tok2.nextToken())-1);

                    current_group.triangles.add(new_tri);
                    tri = new_tri;
                  }
                } else if (tok2.countTokens() == 2) {  /* v/t */

                  tri.vertices[0] = vertices.get(Integer.parseInt(tok2.nextToken())-1);
                  if (hastexture) {
                    tri.vertex_tex_coords[0] = textures_coord.get(Integer.parseInt(tok2.nextToken())-1);
                  }

                  tok2 = new StringTokenizer(tok.nextToken(), "/");
                  tri.vertices[1] = vertices.get(Integer.parseInt(tok2.nextToken())-1);
                  if (hastexture) {
                    tri.vertex_tex_coords[1] = textures_coord.get(Integer.parseInt(tok2.nextToken())-1);
                  }

                  tok2 = new StringTokenizer(tok.nextToken(), "/");
                  tri.vertices[2] = vertices.get(Integer.parseInt(tok2.nextToken())-1);
                  if (hastexture) {
                    tri.vertex_tex_coords[2] = textures_coord.get(Integer.parseInt(tok2.nextToken())-1);
                  }

                  current_group.triangles.add(tri);

                  while (tok.hasMoreTokens()) {
                    Triangle new_tri = new Triangle();
                    tok2 = new StringTokenizer(tok.nextToken(), "/");

                    new_tri.vertices[0] = tri.vertices[0];
                    new_tri.vertices[1] = tri.vertices[2];
                    new_tri.vertices[2] = vertices.get(Integer.parseInt(tok2.nextToken())-1);

                    if (hastexture) {
                      new_tri.vertex_tex_coords[0] = tri.vertex_tex_coords[0];
                      new_tri.vertex_tex_coords[1] = tri.vertex_tex_coords[2];
                      new_tri.vertex_tex_coords[2] = textures_coord.get(Integer.parseInt(tok2.nextToken())-1);
                    }

                    current_group.triangles.add(new_tri);
                    tri = new_tri;
                  }
                } else {/* v */

                  tok = new StringTokenizer(line, " ");
                  tri.vertices[0] = vertices.get(Integer.parseInt(tok.nextToken())-1);
                  tri.vertices[1] = vertices.get(Integer.parseInt(tok.nextToken())-1);

                  if (tok.hasMoreTokens()) {
                    tri.vertices[2] = vertices.get(Integer.parseInt(tok.nextToken())-1);
                  } else {
                    tri.vertices[2] = tri.vertices[0];
                  }

                  current_group.triangles.add(tri);

                  while (tok.hasMoreTokens()) {
                    Triangle new_tri = new Triangle();

                    new_tri.vertices[0] = tri.vertices[0];
                    new_tri.vertices[1] = tri.vertices[2];
                    new_tri.vertices[2] = vertices.get(Integer.parseInt(tok.nextToken())-1);

                    current_group.triangles.add(new_tri);
                    tri = new_tri;
                  }
                }
              }
              break;
            case 's':
              //ignored
              break;
            default:
              Logger.getLogger(JWavefrontObject.class.getName()).log(Level.WARNING,
                      "parse() error: line not recognized >> " + line, file.getName());
              break;
          }
        }
      }
    } catch (java.util.NoSuchElementException e) {
      System.out.println(line);
      throw new IOException(e);
    } catch (FileNotFoundException ex) {
      throw new IOException(ex.getMessage());
    } finally {
      if (in != null) {
        try {
          in.close();
        } catch (IOException ex) {
          Logger.getLogger(JWavefrontObject.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
    }

        //calculate the face normals for the triangles of each group
        for (int i = 0; i < groups.size(); i++) {
          groups.get(i).calculate_face_normals();
        }

        //if the vertex normals are not presented, calculate them
        if (normals.isEmpty()) {
          calculate_vertex_normals();
        }
        
        calculate_vertex_tangents();
    }

  /**
   * Find a group in the model.
   *
   * @param name Group name.
   * @return Return the first group with the given name.
   */
  public Group findGroup(String name) {
      if (name == null)
          return null;
      
    for (int i = 0; i < groups.size(); i++) {
      if (groups.get(i).name.toLowerCase().equals(name.toLowerCase())) {
        return groups.get(i);
      }
    }
    return null;
  }

  /**
   * Find a material in the model.
   *
   * @param name Material name.
   * @return Return the first material with the given name.
   */
  private Material findMaterial(String name) {
    /*
     * XXX doing a linear search on a string key'd list is pretty lame, but
     * it works and is fast enough for now.
     */
    for (int i = 0; i < materials.size(); i++) {
      if (materials.get(i).name.toLowerCase().equals(name.toLowerCase())) {
        return materials.get(i);
      }
    }

    return Material.default_material;
  }

    /**
     * Find a texture in the model.
     *
     * @param name Texture name.
     * @return Return the first texture with the given name.
     */
    private Texture findTexture(String name) throws IOException {
        /*
         * XXX doing a linear search on a string key'd list is pretty lame, but
         * it works and is fast enough for now.
         */
        
        String lowerCaseName = name.toLowerCase();
        for (int i = 0; i < textures.size(); i++) {
            if (textures.get(i).name.toLowerCase().equals(lowerCaseName)) {
                return textures.get(i);
            }
        }

        return null;
    }

  /**
   * Read a wavefront material library file.
   *
   * @param name The filename of the material file
   * @throws IOException
   */
  private void parse_mtl(String name) throws IOException {
    File file = new File(pathname.getParent() + "/" + name);
    float alpha = 1.0f;

    if (file.exists()) {
      BufferedReader in = null;
      StringTokenizer tok;

      try {
        in = new BufferedReader(new FileReader(file));
        Material material = null;

        String line;
        while ((line = in.readLine()) != null) {
          line = line.trim();

          if (line.length() > 0) {
            switch (line.charAt(0)) {
              case '#': /* comment */

                break;
              case 'n': /* newmtl */

                tok = new StringTokenizer(line, " ");
                String token = tok.nextToken(); //ignores newmtl

                //creating the new material
                if (token.equals("newmtl")) {
                  material = new Material(tok.nextToken());
                  materials.add(material);
                } else {
                  Logger.getLogger(JWavefrontObject.class.getName()).log(Level.WARNING,
                          "parse_mtl() error: line not recognized >> " + line, file.getName());
                }

                break;
              case 'N': /* Ni or Ns */

                switch (line.charAt(1)) {
                  case 'i': /* ignored */

                    break;
                  case 's':
                    tok = new StringTokenizer(line, " ");
                    tok.nextToken(); //ignores Ns
                    material.shininess = Float.parseFloat(tok.nextToken());

                    /*
                     * wavefront shininess is from [0,
                     * 1000], so scale for OpenGL
                     */
                    material.shininess = (material.shininess / 1000.0f) * 128.0f;
                    break;
                  default:
                    Logger.getLogger(JWavefrontObject.class.getName()).log(Level.WARNING,
                            "parse_mtl() error: line not recognized >> " + line, file.getName());
                }
                break;

              case 'K': /* Kd, Ks, or Ka */

                switch (line.charAt(1)) {
                  case 'd':
                    tok = new StringTokenizer(line, " ");
                    tok.nextToken(); //ignores Kd
                    material.diffuse[0] = Float.parseFloat(tok.nextToken());
                    material.diffuse[1] = Float.parseFloat(tok.nextToken());
                    material.diffuse[2] = Float.parseFloat(tok.nextToken());
                    break;
                  case 's':
                    tok = new StringTokenizer(line, " ");
                    tok.nextToken(); //ignores Ks
                    material.specular[0] = Float.parseFloat(tok.nextToken());
                    material.specular[1] = Float.parseFloat(tok.nextToken());
                    material.specular[2] = Float.parseFloat(tok.nextToken());
                    break;
                  case 'a':
                    tok = new StringTokenizer(line, " ");
                    tok.nextToken(); //ignores Ka
                    material.ambient[0] = Float.parseFloat(tok.nextToken());
                    material.ambient[1] = Float.parseFloat(tok.nextToken());
                    material.ambient[2] = Float.parseFloat(tok.nextToken());
                    break;
                  default:
                    Logger.getLogger(JWavefrontObject.class.getName()).log(Level.WARNING,
                            "parse_mtl() error: line not recognized >> " + line, file.getName());
                    break;
                }
                break;
              case 'm': /* map_Kd */

                tok = new StringTokenizer(line, " ");
                token = tok.nextToken(); //ignores map_Kd

                if (token.equals("map_Kd")) {
                  name = tok.nextToken();

                  //loading the texture data
                  Texture texture = findTexture(name);
                  if (texture == null) {
                      texture = TextureHandler.LoadTexture(pathname.getParent() + "/" + name);

                    if (texture != null) {
                      textures.add(texture);
                    } else {
                      Logger.getLogger(JWavefrontObject.class.getName()).log(Level.WARNING,
                              "parse_mtl() error: texture file not found " + name, file.getName());
                    }
                  }

                  material.texture = texture;
                } else if (token.equals("map_Bump")) {
                  name = tok.nextToken();

                  //loading the texture data
                  Texture texture = findTexture(name);
                  if (texture == null) {
                      texture = TextureHandler.LoadTexture(Paths.get(pathname.getParent(), name).toString());

                    if (texture != null) {
                      textures.add(texture);
                    } else {
                      Logger.getLogger(JWavefrontObject.class.getName()).log(Level.WARNING,
                              "parse_mtl() error: texture file not found " + name, file.getName());
                    }
                  }

                  material.texture_normal = texture;
                } else {
                  Logger.getLogger(JWavefrontObject.class.getName()).log(Level.WARNING,
                          "parse_mtl() error: line not recognized >> " + line, file.getName());
                }

                break;
                  
              case 'd':
                  tok = new StringTokenizer(line, " ");
                  tok.nextToken();
                  alpha = Float.parseFloat(tok.nextToken());
                break;  
              default:
                Logger.getLogger(JWavefrontObject.class.getName()).log(Level.WARNING,
                        "parse_mtl() error: line not recognized >> " + line, file.getName());
                break;
            }
          }
        }
      } catch (FileNotFoundException ex) {
        throw new IOException(ex.getMessage());
      } finally {
        if (in != null) {
          try {
            in.close();
          } catch (IOException ex) {
            Logger.getLogger(JWavefrontObject.class.getName()).log(Level.SEVERE, null, ex);
          }
        }
      }
    } else {
      Logger.getLogger(JWavefrontObject.class.getName()).log(Level.WARNING,
              "readMTL() warning: mtl file not found ({0})", file.getName());
    }
  }

    public void drawGroup(Group group)
    {        
        
    }
  
    /**
   * Renders the model to the current OpenGL context using the mode specified.
   *
   * @param gLAutoDrawable The OpenGL context to draw.
   * @param mode a bitwise OR of values describing what is to be rendered.
   * WF_NONE - write only vertices WF_FLAT - write facet normals WF_SMOOTH -
   * write vertex normals WF_TEXTURE - write texture coords WF_FLAT and
   * WF_SMOOTH should not both be specified.
   */
  public void draw()
  {
    for (int i = 0; i < groups.size(); i++) {
        Group group = groups.get(i);
        drawGroup(group);
    }
    gl.glBindVertexArray(0);
    
  }

  public void dispose() {
      
     for (Group g : groups)
     {
         if (g.material.texture == null)
            gl.glDeleteBuffers(2, g.vbo, 0);
         else
            gl.glDeleteBuffers(3, g.vbo, 0);
        
         int vao[] = {g.vao};
         gl.glDeleteVertexArrays(1, vao, 0);
     }
      
    gl.glBindVertexArray(0);
  }

  private void calculate_vertex_normals() {
    float angle = 90;
    Normal[] vertex_normals = new Normal[vertices.size()];

    for (int i = 0; i < groups.size(); i++) {
      Group group = groups.get(i);

      for (int j = 0; j < group.triangles.size(); j++) {
            Triangle triangle = group.triangles.get(j);

            int vertex_index_0 = triangle.vertices[0].id;
            if (vertex_normals[vertex_index_0] == null) {
              vertex_normals[vertex_index_0] = new Normal(triangle.face_normal.x,
                      triangle.face_normal.y, triangle.face_normal.z);
            } else {
              if (should_update_normal(vertex_normals[vertex_index_0],
                      triangle.face_normal, angle)) {
                vertex_normals[vertex_index_0].x += triangle.face_normal.x;
                vertex_normals[vertex_index_0].y += triangle.face_normal.y;
                vertex_normals[vertex_index_0].z += triangle.face_normal.z;
              }
            }

            int vertex_index_1 = triangle.vertices[1].id;
            if (vertex_normals[vertex_index_1] == null) {
              vertex_normals[vertex_index_1] = new Normal(triangle.face_normal.x,
                      triangle.face_normal.y, triangle.face_normal.z);
            } else {
              if (should_update_normal(vertex_normals[vertex_index_1],
                      triangle.face_normal, angle)) {
                vertex_normals[vertex_index_1].x += triangle.face_normal.x;
                vertex_normals[vertex_index_1].y += triangle.face_normal.y;
                vertex_normals[vertex_index_1].z += triangle.face_normal.z;
              }
            }

            int vertex_index_2 = triangle.vertices[2].id;
            if (vertex_normals[vertex_index_2] == null) {
              vertex_normals[vertex_index_2] = new Normal(triangle.face_normal.x,
                      triangle.face_normal.y, triangle.face_normal.z);
            } else {
              if (should_update_normal(vertex_normals[vertex_index_2],
                      triangle.face_normal, angle)) {
                vertex_normals[vertex_index_2].x += triangle.face_normal.x;
                vertex_normals[vertex_index_2].y += triangle.face_normal.y;
                vertex_normals[vertex_index_2].z += triangle.face_normal.z;
              }
            }
      }
    }
    
    // Normalize normals
    for (int i = 0; i < vertex_normals.length; i++) {
        float norm = (float) Math.sqrt(vertex_normals[i].x * vertex_normals[i].x
                + vertex_normals[i].y * vertex_normals[i].y
                + vertex_normals[i].z * vertex_normals[i].z);

        if (norm > 0) {
            vertex_normals[i].x /= norm;
            vertex_normals[i].y /= norm;
            vertex_normals[i].z /= norm;
        }

        normals.add(vertex_normals[i]);
    }

    for (int i = 0; i < groups.size(); i++) {
        Group group = groups.get(i);

        for (int j = 0; j < group.triangles.size(); j++) {
            Triangle triangle = group.triangles.get(j);

            triangle.vertex_normals[0] = vertex_normals[triangle.vertices[0].id];
            triangle.vertex_normals[1] = vertex_normals[triangle.vertices[1].id];
            triangle.vertex_normals[2] = vertex_normals[triangle.vertices[2].id];
        }
    }
  }
  
  
  private void calculate_vertex_tangents() {
        float angle = 90;
        Vector3[] vertex_tangents = new Vector3[vertices.size()];

        for (int i = 0; i < groups.size(); i++) {
          Group group = groups.get(i);

            for (Triangle t : group.triangles) {
                int v0 = t.vertices[0].id;
                int v1 = t.vertices[1].id;
                int v2 = t.vertices[2].id;
                
                if (vertex_tangents[v0] == null)
                    vertex_tangents[v0] = new Vector3();
                if (vertex_tangents[v1] == null)
                    vertex_tangents[v1] = new Vector3();
                if (vertex_tangents[v2] == null)
                    vertex_tangents[v2] = new Vector3();
                
                TextureCoord v0_uv = t.vertex_tex_coords[0];
                TextureCoord v1_uv = t.vertex_tex_coords[1];
                TextureCoord v2_uv = t.vertex_tex_coords[2];

                Vector3 v0_pos = new Vector3(t.vertices[0].x, t.vertices[0].y, t.vertices[0].z);
                Vector3 v1_pos = new Vector3(t.vertices[1].x, t.vertices[1].y, t.vertices[1].z);
                Vector3 v2_pos = new Vector3(t.vertices[2].x, t.vertices[2].y, t.vertices[2].z);

                Vector3 Edge1 = v1_pos.sub(v0_pos);
                Vector3 Edge2 = v2_pos.sub(v0_pos);

                float DeltaU1 = 0;
                float DeltaV1 = 0;
                float DeltaU2 = 0;
                float DeltaV2 = 0;
                if (v1_uv != null && v0_uv != null){
                    DeltaU1 = v1_uv.u - v0_uv.u;
                    DeltaV1 = v1_uv.v - v0_uv.v;
                }
                if (v2_uv != null && v0_uv != null){
                    DeltaU2 = v2_uv.u - v0_uv.u;
                    DeltaV2 = v2_uv.v - v0_uv.v;
                }

                float f = 1.0f / (DeltaU1 * DeltaV2 - DeltaU2 * DeltaV1);

                Vector3 Tangent = new Vector3();

                Tangent.x = f * (DeltaV2 * Edge1.x - DeltaV1 * Edge2.x);
                Tangent.y = f * (DeltaV2 * Edge1.y - DeltaV1 * Edge2.y);
                Tangent.z = f * (DeltaV2 * Edge1.z - DeltaV1 * Edge2.z);

                vertex_tangents[v0] = vertex_tangents[v0].add(Tangent);
                vertex_tangents[v1] = vertex_tangents[v1].add(Tangent);
                vertex_tangents[v2] = vertex_tangents[v2].add(Tangent);
            }
        }
    
        // Normalize tangents
        for (int i = 0; i < vertex_tangents.length; i++) {
            float norm = (float) Math.sqrt(vertex_tangents[i].x * vertex_tangents[i].x
                    + vertex_tangents[i].y * vertex_tangents[i].y
                    + vertex_tangents[i].z * vertex_tangents[i].z);

            if (norm > 0) {
                vertex_tangents[i].x /= norm;
                vertex_tangents[i].y /= norm;
                vertex_tangents[i].z /= norm;
            }
        }

        for (int i = 0; i < groups.size(); i++) {
            Group group = groups.get(i);

            for (int j = 0; j < group.triangles.size(); j++) {
                Triangle triangle = group.triangles.get(j);

                triangle.tangents[0] = vertex_tangents[triangle.vertices[0].id];
                triangle.tangents[1] = vertex_tangents[triangle.vertices[1].id];
                triangle.tangents[2] = vertex_tangents[triangle.vertices[2].id];
            }
        }
    }

  private boolean should_update_normal(Normal a, Normal b, float angle) {
    float cos_angle = (float) Math.cos(Math.toRadians(angle));
    float dot = a.x * b.x + a.y * b.y + a.z * b.z;
    return (dot > cos_angle);
  }
  
    public void dump() {
      for (int i = 0; i < groups.size(); i++) {
        System.out.println("----");
        groups.get(i).dump();
      }
    }

    public BoundingBox getBoundingBox() {
        
        BoundingBox min = groups.get(0).getBoundingBox();
        for (int i = 1; i < groups.size(); i++) {
            min.expand(groups.get(i).getBoundingBox());
        }
        
        return min;
    }

    public ArrayList<Group> getGroups() {
        return groups;
    }
}
