import numpy as np


class PointCloud(object):

    ply_header = (
                '''ply
                format ascii 1.0
                element vertex {vertex_count}
                property float x
                property float y
                property float z
                property uchar red
                property uchar green
                property uchar blue
                end_header
                ''')

    def __init__(self, coordinates, colors):
        self.coordinates = coordinates.reshape(-1, 3)
        self.colors = colors.reshape(-1, 3)

    def write_ply(self, output_file):
        points = np.hstack([self.coordinates, self.colors])
        with open(output_file, 'w') as outfile:
            outfile.write(self.ply_header.format(
                                            vertex_count=len(self.coordinates)))
            np.savetxt(outfile, points, '%f %f %f %d %d %d')

    def filter_infinity(self):
        mask = self.coordinates[:, 2] > self.coordinates[:, 2].min()
        coords = self.coordinates[mask]
        colors = self.colors[mask]
        return PointCloud(coords, colors)
